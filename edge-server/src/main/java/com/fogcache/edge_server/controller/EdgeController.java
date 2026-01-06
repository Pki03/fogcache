package com.fogcache.edge_server.controller;

import com.fogcache.edge_server.analytics.PatternAnalyzer;
import com.fogcache.edge_server.analytics.RequestPattern;
import com.fogcache.edge_server.cache.CacheStore;
import com.fogcache.edge_server.cache.LRUCache;
import com.fogcache.edge_server.logging.RequestLog;
import com.fogcache.edge_server.logging.RequestLogger;
import com.fogcache.edge_server.replication.AdaptiveReplicationManager;
import com.fogcache.edge_server.replication.ReplicationRequest;
import com.fogcache.edge_server.replication.ReplicationService;
import com.fogcache.edge_server.ratelimit.TokenBucketRateLimiter;
import org.springframework.beans.factory.annotation.Value;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class EdgeController {

    private final CacheStore cache;
    private final RestTemplate restTemplate = new RestTemplate();

    private final ReplicationService replicationService;
    private final AdaptiveReplicationManager adaptiveManager;
    private final RequestLogger requestLogger;

    private final TokenBucketRateLimiter rateLimiter = new TokenBucketRateLimiter();
    private final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();

    private final String CURRENT_NODE;

    @Autowired
    private PatternAnalyzer analyzer;

    @Value("${fogcache.origin.base-url}")
    private String originBaseUrl;



    // ✅ Single constructor
    public EdgeController(ReplicationService replicationService,
                          AdaptiveReplicationManager adaptiveManager,
                          RequestLogger requestLogger,CacheStore cache,
                          Environment env) {

        this.replicationService = replicationService;
        this.adaptiveManager = adaptiveManager;
        this.requestLogger = requestLogger;
        this.cache = cache;


        String port = env.getProperty("local.server.port", "8080");
        this.CURRENT_NODE = "edge";
    }

    // ------------------- Utility Endpoints -------------------

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @GetMapping("/dump")
    public Map<String, String> dump() {
        return cache.snapshot();
    }

    @GetMapping("/logs")
    public List<RequestLog> getLogs() {
        return requestLogger.getLogs();
    }

    @GetMapping("/patterns")
    public List<RequestPattern> patterns() {
        return analyzer.analyze();
    }

    @PostMapping("/warmup")
    public void warmup(@RequestBody Map<String, String> data) {
        data.forEach(cache::put);
        System.out.println("Warmup completed: " + data.size());
    }

    @PostMapping("/replicate")
    public String replicate(@RequestBody ReplicationRequest req) {
        cache.put(req.getKey(), req.getValue());
        System.out.println("REPLICATED -> " + req.getKey());
        return "OK";
    }

    // ------------------- Core CDN Endpoint -------------------

    @GetMapping("/content")
    public String getContent(@RequestParam String id, HttpServletRequest request) {

        long start = System.currentTimeMillis();
        boolean hit = false;
        String result;

        try {
            if (!rateLimiter.allowRequest(id)) {
                result = "429: Too Many Requests";
            } else {
                Object lock = locks.computeIfAbsent(id, k -> new Object());

                synchronized (lock) {

                    String cached = cache.get(id);
                    if (cached != null) {
                        hit = true;
                        adaptiveManager.onAccess(CURRENT_NODE, id, cached);
                        result = cached;
                    } else {
                        String data = restTemplate.getForObject(
                                originBaseUrl + "/content?id=" + id,
                                String.class
                        );

                        cache.put(id, data);
                        adaptiveManager.onAccess(CURRENT_NODE, id, data);
                        result = data;
                    }
                }
            }
        } catch (Exception e) {
            result = "ERROR";
        }

        long latency = System.currentTimeMillis() - start;
        requestLogger.log(id, CURRENT_NODE, hit, latency,result);

        return result;
    }
}

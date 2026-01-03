package com.fogcache.edge_server.controller;

import com.fogcache.edge_server.cache.CacheStore;
import com.fogcache.edge_server.cache.LRUCache;
import com.fogcache.edge_server.replication.AdaptiveReplicationManager;
import com.fogcache.edge_server.replication.ReplicationRequest;
import com.fogcache.edge_server.replication.ReplicationService;
import com.fogcache.edge_server.ratelimit.TokenBucketRateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class EdgeController {

    private final CacheStore cache = new LRUCache(3);
    private final RestTemplate restTemplate = new RestTemplate();

    private final ReplicationService replicationService;
    private final AdaptiveReplicationManager adaptiveManager;

    private final String CURRENT_NODE;

    // Edge-local rate limiter
    private final TokenBucketRateLimiter rateLimiter = new TokenBucketRateLimiter();

    private final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();

    // ✅ SINGLE correct constructor
    public EdgeController(ReplicationService replicationService,
                          AdaptiveReplicationManager adaptiveManager,
                          Environment env) {

        this.replicationService = replicationService;
        this.adaptiveManager = adaptiveManager;

        String port = env.getProperty("local.server.port");
        if (port == null) port = env.getProperty("server.port", "8080");

        this.CURRENT_NODE = "http://localhost:" + port;
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @Autowired @Qualifier("requestPool")
    private ExecutorService requestPool;

    @Autowired @Qualifier("originPool")
    private ExecutorService originPool;

    @Autowired @Qualifier("replicationPool")
    private ExecutorService replicationPool;


    @GetMapping("/dump")
    public Map<String, String> dump() {
        return cache.snapshot();
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

    @GetMapping("/content")
    public String getContent(@RequestParam String id, HttpServletRequest request) throws Exception {

        Future<String> future = requestPool.submit(() -> {

            if (!rateLimiter.allowRequest(id)) {
                return "429: Too Many Requests";
            }

            Object lock = locks.computeIfAbsent(id, k -> new Object());

            synchronized (lock) {

                String cached = cache.get(id);
                if (cached != null) {
                    adaptiveManager.onAccess(CURRENT_NODE, id, cached);
                    return cached;
                }

                Future<String> originFuture = originPool.submit(() ->
                        restTemplate.getForObject(
                                "http://localhost:8081/content?id=" + id, String.class
                        )
                );

                String data = originFuture.get();

                cache.put(id, data);

                replicationPool.submit(() ->
                        adaptiveManager.onAccess(CURRENT_NODE, id, data)
                );

                return data;
            }
        });

        return future.get();
    }

}

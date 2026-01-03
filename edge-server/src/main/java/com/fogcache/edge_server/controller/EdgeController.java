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
    public ResponseEntity<String> getContent(@RequestParam String id,
                                             HttpServletRequest request) {

        // 🔒 Rate limiting (per key)
        if (!rateLimiter.allowRequest(id)) {
            System.out.println("RATE LIMITED -> " + id);
            return ResponseEntity.status(429).body("Too Many Requests");
        }

        Object lock = locks.computeIfAbsent(id, k -> new Object());

        synchronized (lock) {

            String cached = cache.get(id);
            if (cached != null) {
                System.out.println("HIT -> " + id);

                adaptiveManager.onAccess(CURRENT_NODE, id, cached);
                return ResponseEntity.ok(cached);
            }

            System.out.println("MISS -> " + id);

            String data = restTemplate.getForObject(
                    "http://localhost:8081/content?id=" + id,
                    String.class
            );

            cache.put(id, data);
            adaptiveManager.onAccess(CURRENT_NODE, id, data);

            return ResponseEntity.ok(data);
        }
    }
}

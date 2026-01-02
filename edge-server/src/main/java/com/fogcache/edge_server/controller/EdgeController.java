package com.fogcache.edge_server.controller;

import com.fogcache.edge_server.cache.CacheStore;
import com.fogcache.edge_server.cache.LRUCache;
import com.fogcache.edge_server.replication.ReplicationRequest;
import com.fogcache.edge_server.replication.ReplicationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.env.Environment;


@RestController
public class EdgeController {

    private final CacheStore cache = new LRUCache(3);
    private final RestTemplate restTemplate = new RestTemplate();
    private final ReplicationService replicationService;

    private final String CURRENT_NODE;

    public EdgeController(ReplicationService replicationService, Environment env) {
        this.replicationService = replicationService;

        String port = env.getProperty("local.server.port");
        if (port == null) {
            port = env.getProperty("server.port", "8080");
        }

        this.CURRENT_NODE = "http://localhost:" + port;
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }



    @GetMapping("/content")
    public String getContent(@RequestParam String id) {

        String cached = cache.get(id);
        if (cached != null) {
            System.out.println("HIT -> " + id);
            return cached;
        }

        System.out.println("MISS -> " + id);

        String data = restTemplate.getForObject(
                "http://localhost:8081/content?id=" + id,
                String.class
        );

        cache.put(id, data);

        // 🔁 replicate to other edges
        replicationService.replicate(CURRENT_NODE, id, data);

        return data;
    }

    // Receive replication
    @PostMapping("/replicate")
    public String replicate(@RequestBody ReplicationRequest req) {
        cache.put(req.getKey(), req.getValue());
        System.out.println("REPLICATED -> " + req.getKey());
        return "OK";
    }
}

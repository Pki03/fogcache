package com.fogcache.edge_server.controller;

import com.fogcache.edge_server.cache.CacheStore;
import com.fogcache.edge_server.cache.LRUCache;
import com.fogcache.edge_server.cache.LFUCache;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class EdgeController {

    private final CacheStore cache = new LFUCache(2);
    private final RestTemplate restTemplate = new RestTemplate();

    private long totalRequests = 0;
    private long cacheHits = 0;
    private long cacheMisses = 0;


    @GetMapping("/health")
    public String health() {
        return "OK";
    }


    @GetMapping("/content")
    public String getContent(@RequestParam String id) {

        long start = System.nanoTime();
        totalRequests++;

        String cached = cache.get(id);
        if (cached != null) {
            cacheHits++;
            logStats(start, "HIT", id);
            return cached;
        }
        cacheMisses++;
        String data = restTemplate.getForObject("http://localhost:8081/content?id=" + id, String.class);
        cache.put(id, data);
        logStats(start, "MISS", id);
        return data;
    }

    private void logStats(long start, String result, String key) {
        long latency = (System.nanoTime() - start) / 1_000_000;
        System.out.println("[" + result + "] key=" + key +
                " | latency=" + latency + "ms" +
                " | hitRatio=" + (100.0 * cacheHits / totalRequests) + "%");
    }

    @GetMapping("/metrics")
    public String metrics() {
        return "Requests=" + totalRequests +
                ", Hits=" + cacheHits +
                ", Misses=" + cacheMisses +
                ", HitRatio=" + (100.0 * cacheHits / totalRequests) + "%";
    }


}

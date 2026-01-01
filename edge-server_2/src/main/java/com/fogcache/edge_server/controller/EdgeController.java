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

    @GetMapping("/content")
    public String getContent(@RequestParam String id) {

        String cached = cache.get(id);
        if (cached != null) {
            System.out.println("CACHE HIT -> " + id);
            return cached;
        }
        System.out.println("CACHE MISS -> " + id);


        String originUrl = "http://localhost:8081/content?id=" + id;
        String data = restTemplate.getForObject(originUrl, String.class);

        cache.put(id, data);
        return data;
    }
}

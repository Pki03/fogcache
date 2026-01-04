package com.fogcache.edge_server.prefetch;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PrefetchEngine {

    private final RestTemplate rest = new RestTemplate();

    public void prefetch(String key) {
        try {
            rest.getForObject("http://localhost:8080/content?id=" + key, String.class);
            System.out.println("🚀 PREFETCHED -> " + key);
        } catch (Exception ignored) {}
    }
}

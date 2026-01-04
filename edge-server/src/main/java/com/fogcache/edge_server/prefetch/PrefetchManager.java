package com.fogcache.edge_server.prefetch;

import com.fogcache.edge_server.analytics.PatternAnalyzer;
import com.fogcache.edge_server.analytics.RequestPattern;
import com.fogcache.edge_server.cache.CacheStore;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class PrefetchManager {

    private final PatternAnalyzer analyzer;
    private final CacheStore cache;
    private final RestTemplate rest = new RestTemplate();

    public PrefetchManager(PatternAnalyzer analyzer, CacheStore cache) {
        this.analyzer = analyzer;
        this.cache = cache;
    }

    @Scheduled(fixedDelay = 5000)
    public void prefetch() {

        List<RequestPattern> patterns = analyzer.analyze();

        for (RequestPattern p : patterns) {
            if (shouldPrefetch(p) && cache.get(p.getKey()) == null) {

                String data = rest.getForObject(
                        "http://localhost:8081/content?id=" + p.getKey(),
                        String.class
                );

                cache.put(p.getKey(), data);
                System.out.println("🚀 PREFETCHED -> " + p.getKey());
            }
        }
    }

    private boolean shouldPrefetch(RequestPattern p) {
        return p.getAccessRate() > 1000 && p.getHitRatio() < 0.5;
    }
}

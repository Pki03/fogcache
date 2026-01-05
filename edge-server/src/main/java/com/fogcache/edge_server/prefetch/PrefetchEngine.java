package com.fogcache.edge_server.prefetch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class PrefetchEngine {

    private final RestTemplate rest = new RestTemplate();

    @Value("${fogcache.origin.base-url}")
    private String originBaseUrl;

    @Value("${fogcache.prefetch.cooldown-ms}")
    private long prefetchCooldownMs;

    @Value("${fogcache.prefetch.max-per-window}")
    private int maxPrefetchPerWindow;

    private final Map<String, Long> prefetchHistory = new ConcurrentHashMap<>();
    private final AtomicInteger prefetchCount = new AtomicInteger(0);

    public void prefetch(String key) {

        long now = System.currentTimeMillis();
        Long last = prefetchHistory.get(key);

        // ⏸ Per-key cooldown
        if (last != null && now - last < prefetchCooldownMs) {
            return;
        }

        // 🚫 Global budget
        if (prefetchCount.incrementAndGet() > maxPrefetchPerWindow) {
            System.out.println("⏭ Prefetch skipped (budget exhausted)");
            return;
        }

        try {
            rest.getForObject(
                    originBaseUrl + "/content?id=" + key,
                    String.class
            );
            prefetchHistory.put(key, now);
            System.out.println("🚀 PREFETCHED -> " + key);
        } catch (Exception ignored) {}
    }

    @Scheduled(fixedDelay = 5000)
    public void resetBudget() {
        prefetchCount.set(0);
    }
}

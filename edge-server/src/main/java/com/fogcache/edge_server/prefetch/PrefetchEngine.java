package com.fogcache.edge_server.prefetch;

import com.fogcache.edge_server.cache.CacheStore;
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
    private final CacheStore cache;

    @Value("${fogcache.origin.base-url}")
    private String originBaseUrl;

    @Value("${fogcache.prefetch.cooldown-ms}")
    private long prefetchCooldownMs;

    @Value("${fogcache.prefetch.max-per-window}")
    private int maxPrefetchPerWindow;

    private final Map<String, Long> prefetchHistory = new ConcurrentHashMap<>();
    private final AtomicInteger prefetchCount = new AtomicInteger(0);

    // ✅ Constructor injection (THIS WAS MISSING)
    public PrefetchEngine(CacheStore cache) {
        this.cache = cache;
    }

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
            String data = rest.getForObject(
                    originBaseUrl + "/content?id=" + key,
                    String.class
            );

            // ✅ THIS IS THE CRITICAL LINE
            cache.put(key, data);

            prefetchHistory.put(key, now);
            System.out.println("🚀 PREFETCHED -> " + key);

        } catch (Exception ignored) {}
    }

    @Scheduled(fixedDelay = 5000)
    public void resetBudget() {
        prefetchCount.set(0);
    }
}

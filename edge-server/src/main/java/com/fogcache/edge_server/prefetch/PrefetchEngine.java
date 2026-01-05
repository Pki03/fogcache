package com.fogcache.edge_server.prefetch;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class PrefetchEngine {

    private final RestTemplate rest = new RestTemplate();

    // ✅ DAY 21 GUARDS
    private static final int MAX_PREFETCH_PER_WINDOW = 1;
    private static final long PREFETCH_COOLDOWN_MS = 30_000; // 30 seconds

    private final Map<String, Long> prefetchHistory = new ConcurrentHashMap<>();
    private final AtomicInteger prefetchCount = new AtomicInteger(0);

    public void prefetch(String key) {

        long now = System.currentTimeMillis();
        Long last = prefetchHistory.get(key);

        // ⏸ Per-key cooldown
        if (last != null && now - last < PREFETCH_COOLDOWN_MS) {
            return;
        }

        // 🚫 Global budget
        if (prefetchCount.incrementAndGet() > MAX_PREFETCH_PER_WINDOW) {
            System.out.println("⏭ Prefetch skipped (budget exhausted)");
            return;
        }

        try {
            rest.getForObject(
                    "http://localhost:8080/content?id=" + key,
                    String.class
            );
            prefetchHistory.put(key, now);
            System.out.println("🚀 PREFETCHED -> " + key);
        } catch (Exception ignored) {}
    }

    // 🔁 Reset global budget periodically
    @Scheduled(fixedDelay = 5000)
    public void resetBudget() {
        prefetchCount.set(0);
    }
}

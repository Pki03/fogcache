package com.fogcache.edge_server.metrics;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class HotKeyTracker {

    private static final int HOT_THRESHOLD = 20;

    private final ConcurrentHashMap<String, AtomicInteger> counter = new ConcurrentHashMap<>();

    public boolean record(String key) {
        int count = counter
                .computeIfAbsent(key, k -> new AtomicInteger())
                .incrementAndGet();
        return count >= HOT_THRESHOLD;
    }

    public void reset(String key) {
        counter.remove(key);
    }

    // ✅ Day 22: read-only admin visibility
    public Map<String, Integer> snapshot() {
        Map<String, Integer> snap = new HashMap<>();
        counter.forEach((k, v) -> snap.put(k, v.get()));
        return snap;
    }
}

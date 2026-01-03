package com.fogcache.edge_server.metrics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class HotKeyTracker{

    private static final int HOT_THRESHOLD=20;

    private final ConcurrentHashMap<String, AtomicInteger> counter = new ConcurrentHashMap<>();

    public boolean record(String key) {
        int count = counter.computeIfAbsent(key, k -> new AtomicInteger()).incrementAndGet();
        return count >= HOT_THRESHOLD;
    }

    public void reset(String key) {
        counter.remove(key);
    }

}
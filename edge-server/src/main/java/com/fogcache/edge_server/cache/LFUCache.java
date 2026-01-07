package com.fogcache.edge_server.cache;

import java.util.*;

public class LFUCache implements CacheStore {

    private final int capacity;

    private final Map<String, String> values = new HashMap<>();
    private final Map<String, Integer> frequency = new HashMap<>();

    public LFUCache(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public synchronized String get(String key) {
        if (!values.containsKey(key)) {
            System.out.println("[CACHE MISS] key=" + key);
            return null;
        }

        frequency.put(key, frequency.get(key) + 1);
        System.out.println("[CACHE HIT] key=" + key + " freq=" + frequency.get(key));
        return values.get(key);
    }

    @Override
    public synchronized void put(String key, String value) {
        if (capacity == 0) return;

        if (values.containsKey(key)) {
            values.put(key, value);
            frequency.put(key, frequency.get(key) + 1);
            System.out.println("[CACHE PUT] key=" + key + " freq=" + frequency.get(key));
            logState();
            return;
        }

        if (values.size() >= capacity) {
            evictLFU();
        }

        values.put(key, value);
        frequency.put(key, 1);
        System.out.println("[CACHE PUT] key=" + key + " freq=1");
        logState();
    }

    private void evictLFU() {
        String lfuKey = Collections.min(
                frequency.entrySet(),
                Map.Entry.comparingByValue()
        ).getKey();

        System.out.println("[CACHE EVICT] key=" + lfuKey);

        values.remove(lfuKey);
        frequency.remove(lfuKey);
    }

    private void logState() {
        System.out.println("[CACHE STATE] " + values.keySet());
    }

    @Override
    public synchronized Map<String, String> snapshot() {
        return new HashMap<>(values);
    }
}

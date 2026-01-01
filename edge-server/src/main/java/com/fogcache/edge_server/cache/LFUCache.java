package com.fogcache.edge_server.cache;

import java.util.*;

public class LFUCache implements CacheStore {

    private final int capacity;

    // Stores actual data
    private final Map<String, String> values = new HashMap<>();

    // Stores usage count
    private final Map<String, Integer> frequency = new HashMap<>();

    public LFUCache(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public synchronized String get(String key) {
        if (!values.containsKey(key)) return null;

        frequency.put(key, frequency.get(key) + 1);
        return values.get(key);
    }

    @Override
    public synchronized void put(String key, String value) {
        if (capacity == 0) return;

        if (values.containsKey(key)) {
            values.put(key, value);
            frequency.put(key, frequency.get(key) + 1);
            return;
        }

        if (values.size() >= capacity) {
            evictLFU();
        }

        values.put(key, value);
        frequency.put(key, 1);
    }

    private void evictLFU() {
        String lfuKey = Collections.min(
                frequency.entrySet(),
                Map.Entry.comparingByValue()
        ).getKey();

        System.out.println("EVICTING -> " + lfuKey);


        values.remove(lfuKey);
        frequency.remove(lfuKey);
    }
}

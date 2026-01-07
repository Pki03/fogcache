package com.fogcache.edge_server.cache;

import java.util.*;

public class LRUCache implements CacheStore {

    private final int capacity;
    private final LinkedHashMap<String, String> cache;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new LinkedHashMap<>(capacity, 0.75f, true);
    }

    @Override
    public synchronized String get(String key) {
        if (!cache.containsKey(key)) {
            System.out.println("[CACHE MISS] key=" + key);
            return null;
        }

        System.out.println("[CACHE HIT] key=" + key);
        return cache.get(key);
    }

    @Override
    public synchronized void put(String key, String value) {
        if (cache.containsKey(key)) {
            cache.put(key, value);
            System.out.println("[CACHE PUT] key=" + key);
            logState();
            return;
        }

        if (cache.size() >= capacity) {
            evictLRU();
        }

        cache.put(key, value);
        System.out.println("[CACHE PUT] key=" + key);
        logState();
    }

    private void evictLRU() {
        String lruKey = cache.keySet().iterator().next();
        cache.remove(lruKey);
        System.out.println("[CACHE EVICT] key=" + lruKey);
    }

    private void logState() {
        System.out.println("[CACHE STATE] " + cache.keySet());
    }

    @Override
    public synchronized Map<String, String> snapshot() {
        return new HashMap<>(cache);
    }
}

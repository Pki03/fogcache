package com.fogcache.edge_server.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashMap;


public class LRUCache implements CacheStore {

    private final int capacity;
    private final Map<String, String> cache;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new LinkedHashMap<String, String>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                return size() > LRUCache.this.capacity;
            }
        };
    }

    @Override
    public synchronized String get(String key) {
        return cache.get(key);
    }

    @Override
    public synchronized void put(String key, String value) {
        cache.put(key, value);
    }

    @Override
    public synchronized Map<String, String> snapshot() {
        return new HashMap<>(cache);
    }
}

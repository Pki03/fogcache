package com.fogcache.edge_server.cache;

public interface CacheStore {
    String get(String key);
    void put(String key, String value);
}

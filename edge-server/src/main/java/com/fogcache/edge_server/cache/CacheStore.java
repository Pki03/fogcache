package com.fogcache.edge_server.cache;

import java.util.Map;

public interface CacheStore {
    String get(String key);
    void put(String key, String value);
    Map<String,String> snapshot();
}

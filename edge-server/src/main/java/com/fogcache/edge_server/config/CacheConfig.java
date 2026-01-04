package com.fogcache.edge_server.config;

import com.fogcache.edge_server.cache.CacheStore;
import com.fogcache.edge_server.cache.LRUCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    @Bean
    public CacheStore cacheStore() {
        return new LRUCache(100);
    }
}

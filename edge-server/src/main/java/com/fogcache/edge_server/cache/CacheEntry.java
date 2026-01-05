package com.fogcache.edge_server.cache;

public class CacheEntry {

    private final String value;
    private final long version;

    public CacheEntry(String value, long version) {
        this.value = value;
        this.version = version;
    }

    public String getValue() {
        return value;
    }

    public long getVersion() {
        return version;
    }
}

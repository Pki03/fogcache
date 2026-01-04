package com.fogcache.edge_server.logging;

public class RequestLog {

    private final String key;
    private final String node;
    private final boolean hit;
    private final long latency;
    private final long timestamp;
    private final String value;   // 🆕 store actual data

    public RequestLog(String key,
                      String node,
                      boolean hit,
                      long latency,
                      long timestamp,
                      String value) {
        this.key = key;
        this.node = node;
        this.hit = hit;
        this.latency = latency;
        this.timestamp = timestamp;
        this.value = value;
    }

    public String getKey() { return key; }
    public String getNode() { return node; }
    public boolean isHit() { return hit; }
    public long getLatency() { return latency; }
    public long getTimestamp() { return timestamp; }

    public String getValue() { return value; }   // 🆕
}

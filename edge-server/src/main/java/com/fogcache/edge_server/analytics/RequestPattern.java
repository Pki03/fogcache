package com.fogcache.edge_server.analytics;

public class RequestPattern {

    private final String key;
    private final long count;
    private final double hitRatio;
    private final double accessRate;
    private final String lastValue;

    public RequestPattern(String key,
                          long count,
                          double hitRatio,
                          double accessRate,
                          String lastValue) {
        this.key = key;
        this.count = count;
        this.hitRatio = hitRatio;
        this.accessRate = accessRate;
        this.lastValue = lastValue;
    }

    public String getKey() {
        return key;
    }

    public long getCount() {
        return count;
    }

    public double getHitRatio() {
        return hitRatio;
    }

    public double getAccessRate() {
        return accessRate;
    }

    public String getLastValue() {
        return lastValue;
    }
}

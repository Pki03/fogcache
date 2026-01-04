package com.fogcache.edge_server.ml;

public class FeatureVector {
    public double hitRatio;
    public double accessRate;
    public long count;

    public FeatureVector(double hitRatio, double accessRate, long count) {
        this.hitRatio = hitRatio;
        this.accessRate = accessRate;
        this.count = count;
    }
}

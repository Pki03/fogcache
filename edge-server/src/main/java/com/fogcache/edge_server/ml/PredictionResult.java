package com.fogcache.edge_server.ml;

public class PredictionResult {

    private String clazz;
    private double confidence;

    public PredictionResult() {}

    public PredictionResult(String clazz, double confidence) {
        this.clazz = clazz;
        this.confidence = confidence;
    }

    public String getClazz() {
        return clazz;
    }

    public double getConfidence() {
        return confidence;
    }
}

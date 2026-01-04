package com.fogcache.edge_server.ml;

import com.fogcache.edge_server.analytics.RequestPattern;

public class FeatureExtractor {

    public static FeatureVector extract(RequestPattern p) {
        return new FeatureVector(
                p.getHitRatio(),
                p.getAccessRate(),
                p.getCount()
        );
    }
}

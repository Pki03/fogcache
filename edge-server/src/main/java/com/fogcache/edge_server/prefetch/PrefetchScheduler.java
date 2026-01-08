package com.fogcache.edge_server.prefetch;

import com.fogcache.edge_server.analytics.PatternAnalyzer;
import com.fogcache.edge_server.cache.CacheStore;
import com.fogcache.edge_server.ml.FeatureExtractor;
import com.fogcache.edge_server.ml.MLClient;
import com.fogcache.edge_server.replication.AdaptivePlacementEngine;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;



@Component
public class PrefetchScheduler {

    private final PatternAnalyzer analyzer;
    private final MLClient ml;
    private final AdaptivePlacementEngine placement;
    private final CacheStore cache;

    @Value("${fogcache.ml.enabled:false}")
    private boolean mlEnabled;

    public PrefetchScheduler(
            PatternAnalyzer analyzer,
            MLClient ml,
            AdaptivePlacementEngine placement,
            CacheStore cache
    ) {
        this.analyzer = analyzer;
        this.ml = ml;
        this.placement = placement;
        this.cache = cache;
    }

    @Scheduled(fixedDelay = 5000)
    public void runLearningCycle() {

        if (!mlEnabled) return;

        var patterns = analyzer.analyze();

        for (var p : patterns) {

            var prediction = ml.predict(FeatureExtractor.extract(p));
            if (prediction == null) continue;

            String value = cache.get(p.getKey());
            if (value == null) continue;

            placement.apply(
                    p.getKey(),
                    value,
                    prediction,
                    "local"
            );
        }
    }
}


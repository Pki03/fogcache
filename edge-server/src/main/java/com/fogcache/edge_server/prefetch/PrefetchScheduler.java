package com.fogcache.edge_server.prefetch;

import com.fogcache.edge_server.analytics.PatternAnalyzer;
import com.fogcache.edge_server.ml.FeatureExtractor;
import com.fogcache.edge_server.ml.FeatureVector;
import com.fogcache.edge_server.ml.MLClient;
import com.fogcache.edge_server.ml.PredictionResult;
import com.fogcache.edge_server.replication.AdaptivePlacementEngine;
import com.fogcache.edge_server.cache.CacheStore;

import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Component
@EnableScheduling
public class PrefetchScheduler {

    private final PatternAnalyzer analyzer;
    private final PrefetchEngine prefetch;
    private final MLClient ml;
    private final AdaptivePlacementEngine placement;
    private final CacheStore cache;




    public PrefetchScheduler(PatternAnalyzer analyzer,
                             PrefetchEngine prefetch,
                             MLClient ml,
                             AdaptivePlacementEngine placement,CacheStore cache) {

        this.analyzer = analyzer;
        this.prefetch = prefetch;
        this.ml = ml;
        this.placement = placement;
        this.cache=cache;
    }

    @Scheduled(fixedDelay = 5000)
    public void runLearningCycle() {

        var patterns = analyzer.analyze();

        for (var p : patterns) {

            var features = FeatureExtractor.extract(p);

            var prediction = ml.predict(features);

            if (prediction == null || prediction.getConfidence() < 0.6) continue;

            String value = cache.get(p.getKey());
            if (value == null) return;

            placement.apply(p.getKey(), value, prediction);

            if ("HOT".equals(prediction.getClazz())) {
                prefetch.prefetch(p.getKey());
            }

            System.out.println("🧠 ML -> " + prediction.getClazz() +
                    " (" + prediction.getConfidence() + ") for " + p.getKey());
        }
    }



}

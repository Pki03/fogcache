package com.fogcache.edge_server.replication;

import com.fogcache.edge_server.ml.PredictionResult;
import com.fogcache.edge_server.prefetch.PrefetchEngine;
import com.fogcache.edge_server.routing.RoutingService;
import com.fogcache.edge_server.metrics.HotKeyTracker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class AdaptivePlacementEngine {

    private final ReplicationService replicationService;
    private final RoutingService routingService;
    private final PrefetchEngine prefetchEngine;
    private final HotKeyTracker hotKeyTracker;

    // 🧠 ML decision memory
    private final Map<String, DecisionState> decisionMemory = new ConcurrentHashMap<>();

    // 🔒 Guardrails
    @Value("${fogcache.ml.decision-cooldown-ms}")
    private long decisionCooldownMs;

    @Value("${fogcache.ml.confidence-threshold}")
    private double confidenceThreshold;

    public AdaptivePlacementEngine(
            ReplicationService replicationService,
            RoutingService routingService,
            PrefetchEngine prefetchEngine,
            HotKeyTracker hotKeyTracker
    ) {
        this.replicationService = replicationService;
        this.routingService = routingService;
        this.prefetchEngine = prefetchEngine;
        this.hotKeyTracker = hotKeyTracker;
    }

    public void apply(
            String key,
            String value,
            PredictionResult prediction,
            String currentNode
    ) {

        // 1️⃣ Confidence gate
        if (prediction.getConfidence() < confidenceThreshold) {
            return;
        }

        // 🔥 TRAFFIC PRIORITY RULE
        // If traffic already marked key as HOT, ML cannot cap it as WARM
        if ("WARM".equals(prediction.getClazz()) && hotKeyTracker.isHot(key)) {
            return;
        }

        long now = System.currentTimeMillis();
        DecisionState prev = decisionMemory.get(key);

        // 🚫 Prevent HOT downgrade
        if (prev != null &&
                "HOT".equals(prev.getLastClass()) &&
                !"HOT".equals(prediction.getClazz())) {
            return;
        }

        // ⏸ Cooldown
        if (prev != null && now - prev.getLastUpdated() < decisionCooldownMs) {
            return;
        }

        // 🔁 Idempotency (same class → no action)
        if (prev != null && prev.getLastClass().equals(prediction.getClazz())) {
            prev.touch(now);
            return;
        }

        // 🧠 Persist new decision
        decisionMemory.put(key, new DecisionState(prediction.getClazz(), now));

        // 🎯 Healthy targets (exclude self)
        List<String> targets = routingService.getHealthyNodes()
                .stream()
                .filter(n -> !n.equals(currentNode))
                .collect(Collectors.toList());

        // 2️⃣ Execute ML decision
        switch (prediction.getClazz()) {

            case "HOT" -> {
                System.out.println("🔥 ML HOT → replicate + prefetch: " + key);
                replicationService.replicateToAll(targets, key, value);
                prefetchEngine.prefetch(key + "-next");
            }

            case "WARM" -> {
                System.out.println("♨️ ML WARM → prefetch only: " + key);
                prefetchEngine.prefetch(key + "-next");
            }

            case "COLD" -> {
                System.out.println("❄️ ML COLD → no action: " + key);
            }
        }
    }

    // 🔍 Admin / observability (Phase 22-ready)
    public Map<String, String> decisionSnapshot() {
        return decisionMemory.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().getLastClass()
                ));
    }
}

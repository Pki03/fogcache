package com.fogcache.edge_server.replication;

import com.fogcache.edge_server.ml.PredictionResult;
import com.fogcache.edge_server.routing.RoutingService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AdaptivePlacementEngine {

    private final ReplicationService replicationService;
    private final RoutingService routingService;

    // ✅ DAY 21: decision memory
    private final Map<String, DecisionState> decisionMemory = new ConcurrentHashMap<>();
    private static final long COOLDOWN_MS = 60_000; // 60 seconds

    public AdaptivePlacementEngine(ReplicationService replicationService,
                                   RoutingService routingService) {
        this.replicationService = replicationService;
        this.routingService = routingService;
    }

    // ✅ THIS IS THE ML HANDLING METHOD
    public void apply(String key, String value, PredictionResult p) {

        // 1️⃣ HARD ML CONFIDENCE GATE
        if (p.getConfidence() < 0.65) {
            System.out.println(
                    "🧠 ML ignored (low confidence: " + p.getConfidence() + ") for key=" + key
            );
            return;
        }

        long now = System.currentTimeMillis();

        // 2️⃣ DECISION MEMORY (ANTI-FLAPPING)
        DecisionState prev = decisionMemory.get(key);

        if (prev != null) {

            // Same class → do nothing
            if (prev.getLastClass().equals(p.getClazz())) {
                return;
            }

            // Cooldown not finished → do nothing
            if (now - prev.getLastUpdated() < COOLDOWN_MS) {
                System.out.println("⏸ Cooldown active for key=" + key);
                return;
            }
        }

        // 3️⃣ UPDATE DECISION MEMORY
        decisionMemory.putIfAbsent(
                key,
                new DecisionState(p.getClazz(), now)
        );
        decisionMemory.get(key).update(p.getClazz());

        // 4️⃣ SAFE REPLICATION
        List<String> nodes = routingService.getHealthyNodes();

        switch (p.getClazz()) {

            case "HOT" -> {
                System.out.println("🔥 HOT key -> replicate to all: " + key);
                replicationService.replicateToAll(nodes, key, value);
            }

            case "WARM" -> {
                System.out.println("♨️ WARM key -> replicate to neighbors: " + key);
                replicationService.replicateToNeighbors(nodes, key, value);
            }

            default -> {
                // COLD → no replication
            }
        }
    }
}

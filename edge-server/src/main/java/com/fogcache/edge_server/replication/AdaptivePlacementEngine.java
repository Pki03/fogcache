package com.fogcache.edge_server.replication;

import com.fogcache.edge_server.ml.PredictionResult;
import com.fogcache.edge_server.routing.RoutingService;
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

    // ✅ Decision memory
    private final Map<String, DecisionState> decisionMemory = new ConcurrentHashMap<>();

    // ✅ Config-driven controls (Day 23)
    @Value("${fogcache.ml.decision-cooldown-ms}")
    private long decisionCooldownMs;

    @Value("${fogcache.ml.confidence-threshold}")
    private double confidenceThreshold;

    public AdaptivePlacementEngine(
            ReplicationService replicationService,
            RoutingService routingService
    ) {
        this.replicationService = replicationService;
        this.routingService = routingService;
    }

    // ✅ ML handling
    public void apply(String key, String value, PredictionResult p) {

        // 1️⃣ Confidence gate
        if (p.getConfidence() < confidenceThreshold) {
            System.out.println(
                    "🧠 ML ignored (low confidence: " + p.getConfidence() + ") for key=" + key
            );
            return;
        }

        long now = System.currentTimeMillis();

        // 2️⃣ Anti-flapping via decision memory
        DecisionState prev = decisionMemory.get(key);

        if (prev != null) {

            // Same class → no-op
            if (prev.getLastClass().equals(p.getClazz())) {
                return;
            }

            // Cooldown active → no-op
            if (now - prev.getLastUpdated() < decisionCooldownMs) {
                System.out.println("⏸ Cooldown active for key=" + key);
                return;
            }
        }

        // 3️⃣ Update decision memory
        decisionMemory.putIfAbsent(key, new DecisionState(p.getClazz(), now));
        decisionMemory.get(key).update(p.getClazz());

        // 4️⃣ Replication decision
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

    // ✅ Day 22: ML decision snapshot (read-only)
    public Map<String, String> decisionSnapshot() {
        return decisionMemory.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().getLastClass()
                ));
    }
}

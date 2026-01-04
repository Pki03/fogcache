package com.fogcache.edge_server.replication;

import com.fogcache.edge_server.ml.PredictionResult;
import org.springframework.stereotype.Component;

@Component
public class AdaptivePlacementEngine {

    private final ReplicationService replication;

    public AdaptivePlacementEngine(ReplicationService replication) {
        this.replication = replication;
    }

    public void apply(String key, String value, PredictionResult p) {

        if ("HOT".equals(p.getClazz())) {
            replication.replicateToAll(key, value);
        }
        else if ("WARM".equals(p.getClazz())) {
            replication.replicateToNeighbors(key, value);
        }
        // COLD → no replication
    }
}

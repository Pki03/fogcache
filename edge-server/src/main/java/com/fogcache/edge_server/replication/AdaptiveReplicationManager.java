package com.fogcache.edge_server.replication;

import com.fogcache.edge_server.metrics.HotKeyTracker;
import org.springframework.stereotype.Component;

@Component
public class AdaptiveReplicationManager {

    private final ReplicationService replicationService;
    private final HotKeyTracker tracker = new HotKeyTracker();

    public AdaptiveReplicationManager(ReplicationService replicationService) {
        this.replicationService = replicationService;
    }

    public void onAccess(String node, String key, String value) {
        boolean hot = tracker.record(key);

        if (hot) {
            System.out.println("🔥 HOT KEY DETECTED -> " + key);
            replicationService.replicate(node, key, value);
            tracker.reset(key);
        }
    }
}

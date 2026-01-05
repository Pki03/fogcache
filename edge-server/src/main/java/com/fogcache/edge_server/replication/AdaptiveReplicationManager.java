package com.fogcache.edge_server.replication;

import com.fogcache.edge_server.metrics.HotKeyTracker;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AdaptiveReplicationManager {

    private final ReplicationService replicationService;
    private final HotKeyTracker tracker = new HotKeyTracker();

    // ✅ DAY 21 FIX: cooldown for hot-key detection
    private final Map<String, Long> hotCooldown = new ConcurrentHashMap<>();
    private static final long HOT_COOLDOWN_MS = 30_000; // 30 seconds

    public AdaptiveReplicationManager(ReplicationService replicationService) {
        this.replicationService = replicationService;
    }

    public void onAccess(String node, String key, String value) {

        boolean hot = tracker.record(key);

        if (!hot) {
            return;
        }

        long now = System.currentTimeMillis();
        Long last = hotCooldown.get(key);

        // ⏸ Cooldown active → ignore
        if (last != null && now - last < HOT_COOLDOWN_MS) {
            return;
        }

        // ✅ Mark hot & act ONCE per window
        hotCooldown.put(key, now);

        System.out.println("🔥 HOT KEY DETECTED -> " + key);
        replicationService.replicate(node, key, value);

        // ❌ DO NOT reset tracker
        // tracker.reset(key);  ← REMOVE THIS
    }
}

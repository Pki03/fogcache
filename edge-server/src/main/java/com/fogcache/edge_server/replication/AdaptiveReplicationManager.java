package com.fogcache.edge_server.replication;

import com.fogcache.edge_server.metrics.HotKeyTracker;
import com.fogcache.edge_server.prefetch.PrefetchEngine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AdaptiveReplicationManager {

    private final ReplicationService replicationService;
    private final HotKeyTracker tracker;
    private final PrefetchEngine prefetchEngine;

    @Value("${fogcache.ml.enabled:false}")
    private boolean mlEnabled;   // 🔑 ML has authority when enabled

    private final Map<String, Long> hotCooldown = new ConcurrentHashMap<>();
    private static final long HOT_COOLDOWN_MS = 30_000;

    public AdaptiveReplicationManager(
            ReplicationService replicationService,
            HotKeyTracker tracker,
            PrefetchEngine prefetchEngine
    ) {
        this.replicationService = replicationService;
        this.tracker = tracker;
        this.prefetchEngine = prefetchEngine;
    }

    public void onAccess(String currentNode, String key, String value) {

        // 🚨 Phase 19 rule: ML overrides rule-based logic
        if (mlEnabled) return;

        boolean hot = tracker.record(key);
        if (!hot) return;

        long now = System.currentTimeMillis();
        Long last = hotCooldown.get(key);

        if (last != null && now - last < HOT_COOLDOWN_MS) return;
        hotCooldown.put(key, now);

        System.out.println("🚀 RULE HOT → replicate + prefetch: " + key);
        replicationService.replicateToPeers(currentNode, key, value);
        prefetchEngine.prefetch(key + "-next");
    }
}

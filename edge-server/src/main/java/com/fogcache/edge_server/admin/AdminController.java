package com.fogcache.edge_server.admin;

import com.fogcache.edge_server.metrics.EdgeMetrics;
import com.fogcache.edge_server.metrics.HotKeyTracker;
import com.fogcache.edge_server.replication.AdaptivePlacementEngine;
import com.fogcache.edge_server.routing.RoutingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final RoutingService routingService;
    private final HotKeyTracker hotKeyTracker;
    private final AdaptivePlacementEngine placementEngine;
    private final EdgeMetrics metrics;

    public AdminController(
            RoutingService routingService,
            HotKeyTracker hotKeyTracker,
            AdaptivePlacementEngine placementEngine,
            EdgeMetrics metrics
    ) {
        this.routingService = routingService;
        this.hotKeyTracker = hotKeyTracker;
        this.placementEngine = placementEngine;
        this.metrics = metrics;
    }

    // ✅ Cluster nodes (read-only)
    @GetMapping("/nodes")
    public List<String> nodes() {
        return routingService.getHealthyNodes();
    }

    // ✅ Hot key visibility
    @GetMapping("/hotkeys")
    public Map<String, Integer> hotKeys() {
        return hotKeyTracker.snapshot();
    }

    // ✅ ML decision visibility
    @GetMapping("/ml/decisions")
    public Map<String, String> mlDecisions() {
        return placementEngine.decisionSnapshot();
    }

    // ✅ Metrics visibility (Phase 22 requirement)
    @GetMapping("/metrics")
    public Map<String, Object> metrics() {
        return metrics.snapshot();
    }
}

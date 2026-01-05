package com.fogcache.edge_server.admin;

import com.fogcache.edge_server.metrics.HotKeyTracker;
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

    public AdminController(
            RoutingService routingService,
            HotKeyTracker hotKeyTracker
    ) {
        this.routingService = routingService;
        this.hotKeyTracker = hotKeyTracker;
    }

    // ✅ Cluster view
    @GetMapping("/nodes")
    public List<String> nodes() {
        return routingService.getHealthyNodes();
    }

    // ✅ Hot key visibility (Day 22)
    @GetMapping("/hotkeys")
    public Map<String, Integer> hotKeys() {
        return hotKeyTracker.snapshot();
    }
}

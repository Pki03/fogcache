package com.fogcache.load_balancer.controller;

import com.fogcache.load_balancer.cluster.NodeRegistry;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nodes")
public class NodeController {

    private final NodeRegistry registry;

    public NodeController(NodeRegistry registry) {
        this.registry = registry;
    }

    @GetMapping("/healthy")
    public List<String> healthyNodes() {
        return registry.getHealthyNodes();
    }
}

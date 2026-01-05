package com.fogcache.load_balancer.controller;

import com.fogcache.load_balancer.cluster.NodeRegistry;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LBController {

    private final NodeRegistry registry;
    private final RestTemplate rest = new RestTemplate();

    public LBController(NodeRegistry registry) {
        this.registry = registry;
    }

    @GetMapping("/content")
    public String route(@RequestParam String id) {

        List<String> nodes = registry.getHealthyNodes();

        if (nodes.isEmpty()) {
            throw new RuntimeException("No healthy nodes available");
        }

        for (String node : nodes) {
            try {
                return rest.getForObject(
                        node + "/content?id=" + id,
                        String.class
                );
            }
            catch (Exception e) {
                System.out.println("⚠️ Failed node: " + node + " — trying next...");
                registry.markDown(node);
            }
        }

        throw new RuntimeException("All nodes failed");
    }
}

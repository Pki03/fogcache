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

        System.out.println("🔵 [LB] Incoming request id=" + id);

        List<String> nodes = registry.getHealthyNodes();

        System.out.println("📋 [LB] Healthy nodes = " + nodes);

        if (nodes.isEmpty()) {
            throw new RuntimeException("No healthy nodes available");
        }

        for (String node : nodes) {
            try {
                System.out.println("🟢 [LB] Routing id=" + id + " to " + node);

                String response = rest.getForObject(
                        node + "/content?id=" + id,
                        String.class
                );

                System.out.println("🟣 [LB] Success from " + node);

                return response;

            } catch (Exception e) {
                System.out.println("⚠️ [LB] Failed node: " + node + " — marking DOWN");
                registry.markDown(node);
            }
        }

        throw new RuntimeException("All nodes failed");
    }

}

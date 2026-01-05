package com.fogcache.edge_server.cluster;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HealthMonitor {

    private final NodeRegistry registry;
    private final RestTemplate rest = new RestTemplate();

    public HealthMonitor(NodeRegistry registry) {
        this.registry = registry;
    }

    @Scheduled(fixedDelay = 3000)
    public void checkHealth() {

        for (String node : registry.getNodes()) {
            try {
                rest.getForObject(node + "/health", String.class);
                System.out.println("🟢 Healthy: " + node);
            } catch (Exception e) {
                System.out.println("🔴 Dead: " + node);
                registry.remove(node);
            }
        }
    }
}

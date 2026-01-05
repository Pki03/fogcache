package com.fogcache.load_balancer.cluster;

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

        for (String node : registry.getAllNodes()) {
            try {
                rest.getForObject(node + "/health", String.class);
                registry.markUp(node);
            } catch (Exception e) {
                registry.markDown(node);
            }
        }
    }
}

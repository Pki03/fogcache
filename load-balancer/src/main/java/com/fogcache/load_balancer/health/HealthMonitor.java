package com.fogcache.load_balancer.health;

import com.fogcache.load_balancer.hashing.HashRing;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HealthMonitor {

    private final HashRing ring;
    private final RestTemplate restTemplate = new RestTemplate();

    public HealthMonitor(HashRing ring) {
        this.ring = ring;
    }

    @Scheduled(fixedDelay = 3000)
    public void checkHealth() {
        ring.getAllNodes().forEach(node -> {
            try {
                restTemplate.getForObject(node + "/health", String.class);
                ring.markUp(node);
            } catch (Exception e) {
                ring.markDown(node);
            }
        });
    }
}

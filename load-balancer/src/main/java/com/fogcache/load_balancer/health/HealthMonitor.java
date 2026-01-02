package com.fogcache.load_balancer.health;

import com.fogcache.load_balancer.hashing.HashRing;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class HealthMonitor {

    private final HashRing ring;
    private final RestTemplate restTemplate = new RestTemplate();

    // Track consecutive failures
    private final Map<String, Integer> failureCount = new ConcurrentHashMap<>();
    private static final int MAX_FAILURES = 3;

    public HealthMonitor(HashRing ring) {
        this.ring = ring;
    }

    @Scheduled(fixedDelay = 3000)
    public void checkHealth() {
        ring.getAllNodes().forEach(node -> {
            try {
                restTemplate.getForObject(node + "/health", String.class);

                // success → reset failure counter
                failureCount.put(node, 0);
                ring.markUp(node);

            } catch (Exception e) {
                int fails = failureCount.getOrDefault(node, 0) + 1;
                failureCount.put(node, fails);

                if (fails >= MAX_FAILURES) {
                    ring.markDown(node);
                    System.out.println("Node DOWN -> " + node);
                }
            }
        });
    }
}

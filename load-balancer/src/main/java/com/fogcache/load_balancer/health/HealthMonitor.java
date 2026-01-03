package com.fogcache.load_balancer.health;

import com.fogcache.load_balancer.hashing.HashRing;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.*;


@Component
public class HealthMonitor {

    private final HashRing ring;
    private final RestTemplate restTemplate = new RestTemplate();
    private final Set<String> knownUp = ConcurrentHashMap.newKeySet();

    public HealthMonitor(HashRing ring) {
        this.ring = ring;
    }

    @Scheduled(fixedDelay = 3000)
    public void checkHealth() {
        for (String node : ring.getAllNodes()) {
            try {
                restTemplate.getForObject(node + "/health", String.class);

                if (!knownUp.contains(node)) {
                    System.out.println("Node UP -> " + node);
                    ring.markUp(node);
                    knownUp.add(node);
                    warmupNode(node);
                }

            } catch (Exception e) {
                if (knownUp.contains(node)) {
                    System.out.println("Node DOWN -> " + node);
                    ring.markDown(node);
                    knownUp.remove(node);
                }
            }
        }
    }

    private void warmupNode(String recoveringNode) {
        if (knownUp.isEmpty()) return;

        String donor = knownUp.stream()
                .filter(n -> !n.equals(recoveringNode))
                .findFirst()
                .orElse(null);

        if (donor == null) return;

        Map<String, String> snapshot =
                restTemplate.getForObject(donor + "/dump", Map.class);

        if (snapshot != null && !snapshot.isEmpty())
        {
            restTemplate.postForObject(
                    recoveringNode + "/warmup",
                    snapshot,
                    String.class
            );
        }
    }
}

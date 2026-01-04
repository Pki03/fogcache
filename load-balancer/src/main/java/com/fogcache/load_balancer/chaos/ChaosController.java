package com.fogcache.load_balancer.chaos;

import org.springframework.stereotype.Component;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChaosController {

    private final Set<String> deadNodes = ConcurrentHashMap.newKeySet();

    public void kill(String node) {
        deadNodes.add(node);
        System.out.println("🔥 CHAOS: Killing " + node);
    }

    public void revive(String node) {
        deadNodes.remove(node);
        System.out.println("💚 CHAOS: Reviving " + node);
    }

    public boolean isDead(String node) {
        return deadNodes.contains(node);
    }
}

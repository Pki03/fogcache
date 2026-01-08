package com.fogcache.load_balancer.cluster;

import com.fogcache.load_balancer.hashing.HashRing;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NodeRegistry {

    private final Set<String> allNodes = ConcurrentHashMap.newKeySet();
    private final Set<String> healthyNodes = ConcurrentHashMap.newKeySet();

    private final HashRing hashRing;

    public NodeRegistry(HashRing hashRing) {
        this.hashRing = hashRing;

        // initial cluster
        registerNode("http://localhost:8082");
        registerNode("http://localhost:8083");
    }

    // ─────────────────────────────────────────────

    private void registerNode(String node) {
        allNodes.add(node);
        healthyNodes.add(node);
        hashRing.addNode(node);   // 🔑 CRITICAL LINE
    }

    public Set<String> getAllNodes() {
        return allNodes;
    }

    public List<String> getHealthyNodes() {
        return new ArrayList<>(healthyNodes);
    }

    public void markUp(String node) {
        healthyNodes.add(node);
        hashRing.markUp(node);
    }

    public void markDown(String node) {
        healthyNodes.remove(node);
        hashRing.markDown(node);
    }
}

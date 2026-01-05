package com.fogcache.load_balancer.cluster;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class NodeRegistry {

    private final Set<String> allNodes = ConcurrentHashMap.newKeySet();
    private final Set<String> healthyNodes = ConcurrentHashMap.newKeySet();

    public NodeRegistry() {
        // initial cluster
        allNodes.add("http://localhost:8082");
        allNodes.add("http://localhost:8083");

        healthyNodes.addAll(allNodes);
    }

    public Set<String> getAllNodes() {
        return allNodes;
    }


    public List<String> getHealthyNodes() {
        return new ArrayList<>(healthyNodes);
    }

    public void markUp(String node) {
        healthyNodes.add(node);
    }

    public void markDown(String node) {
        healthyNodes.remove(node);
    }
}

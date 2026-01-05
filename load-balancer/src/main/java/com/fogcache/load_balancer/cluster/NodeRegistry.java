package com.fogcache.edge_server.cluster;

import org.springframework.stereotype.Component;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NodeRegistry {

    private final Set<String> nodes = ConcurrentHashMap.newKeySet();

    public void register(String node) {
        nodes.add(node);
    }

    public void remove(String node) {
        nodes.remove(node);
    }

    public Set<String> getNodes() {
        return nodes;
    }

    public int size() {
        return nodes.size();
    }
}

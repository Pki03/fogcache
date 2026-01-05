package com.fogcache.edge_server.replication;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ReplicationService {

    private final RestTemplate rest = new RestTemplate();

    // 🔁 Basic single-node replication
    public void replicate(String targetNode, String key, String value) {
        ReplicationRequest req = new ReplicationRequest(key, value);
        rest.postForObject(targetNode + "/replicate", req, String.class);
    }

    // 🌍 Replicate to all provided nodes
    public void replicateToAll(List<String> nodes, String key, String value) {
        for (String node : nodes) {
            replicate(node, key, value);
        }
    }

    // 🧠 Fault-tolerant quorum replication
    public void replicateWithQuorum(List<String> nodes, String key, String value) {

        int success = 0;
        int required = nodes.size() / 2 + 1;

        for (String node : nodes) {
            try {
                replicate(node, key, value);
                success++;
            } catch (Exception ignored) {}
        }

        if (success < required) {
            throw new RuntimeException("❌ Quorum not met");
        }
    }

    // 🧩 Neighbor replication (used by ML placement)
    public void replicateToNeighbors(List<String> nodes, String key, String value) {

        int sent = 0;
        for (String node : nodes) {
            replicate(node, key, value);
            sent++;
            if (sent >= 2) break;  // simple "nearest two" policy
        }
    }
}

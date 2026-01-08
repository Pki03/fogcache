package com.fogcache.edge_server.replication;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ReplicationService {

    private final RestTemplate rest = new RestTemplate();

    // 🌍 Known edge peers (static for now)
    private final List<String> peers = List.of(
            "http://localhost:8082",
            "http://localhost:8083"
    );

    // -------------------------------------------------
    // 🔁 BASIC REPLICATION (used internally)
    // -------------------------------------------------
    public void replicate(String targetNode, String key, String value) {
        ReplicationRequest req = new ReplicationRequest(key, value);
        rest.postForObject(targetNode + "/replicate", req, String.class);
    }

    // -------------------------------------------------
    // 🚀 PHASE 9: Replicate to peers EXCEPT self
    // -------------------------------------------------
    public void replicateToPeers(String currentNode, String key, String value) {

        for (String peer : peers) {
            if (peer.equals(currentNode)) continue;

            try {
                replicate(peer, key, value);
                System.out.println("REPLICATED -> " + key + " to " + peer);
            } catch (Exception e) {
                System.out.println("⚠️ Replication failed to " + peer);
            }
        }
    }

    // -------------------------------------------------
    // 🌍 EXISTING API (DO NOT BREAK)
    // -------------------------------------------------
    public void replicateToAll(List<String> nodes, String key, String value) {
        for (String node : nodes) {
            replicate(node, key, value);
        }
    }

    // -------------------------------------------------
    // 🧠 QUORUM REPLICATION
    // -------------------------------------------------
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

    // -------------------------------------------------
    // 🧩 NEIGHBOR REPLICATION (ML placement)
    // -------------------------------------------------
    public void replicateToNeighbors(List<String> nodes, String key, String value) {

        int sent = 0;
        for (String node : nodes) {
            replicate(node, key, value);
            sent++;
            if (sent >= 2) break;
        }
    }
}

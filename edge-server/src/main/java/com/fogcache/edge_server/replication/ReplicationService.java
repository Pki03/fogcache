package com.fogcache.edge_server.replication;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ReplicationService {

    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final RestTemplate restTemplate = new RestTemplate();

    // Hardcode edges for now
    private final List<String> edgeNodes = List.of(
            "http://localhost:8082",
            "http://localhost:8083"
    );

    public void replicate(String currentNode, String key, String value) {
        for (String node : edgeNodes) {
            if (!node.equals(currentNode)) {
                executor.submit(() -> {
                    try {
                        ReplicationRequest req = new ReplicationRequest(key, value);
                        restTemplate.postForObject(node + "/replicate", req, String.class);
                    } catch (Exception ignored) {}
                });
            }
        }
    }
}

package com.fogcache.edge_server.replication;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class ReplicationService {

    private final RestTemplate restTemplate = new RestTemplate();

    private final List<String> edgeNodes = List.of(
            "http://localhost:8082",
            "http://localhost:8083"
    );

    public void replicate(String source, String key, String value) {
        for (String node : edgeNodes) {
            if (!node.equals(source)) {
                try {
                    restTemplate.postForObject(
                            node + "/replicate",
                            new ReplicationRequest(key, value),
                            String.class
                    );
                } catch (Exception ignored) {
                }
            }
        }
    }

    public void syncNode(String recoveringNode) {
        for (String node : edgeNodes) {
            if (!node.equals(recoveringNode)) {
                try {
                    Map<String, String> data =
                            restTemplate.getForObject(node + "/dump", Map.class);

                    restTemplate.postForObject(
                            recoveringNode + "/warmup",
                            data,
                            Void.class
                    );
                    break;
                } catch (Exception ignored) {
                }
            }
        }
    }
}

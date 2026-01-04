package com.fogcache.edge_server.replication;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ReplicationService {

    private final RestTemplate rest = new RestTemplate();

    public void replicate(String targetNode, String key, String value) {
        ReplicationRequest req = new ReplicationRequest(key, value);
        rest.postForObject(targetNode + "/replicate", req, String.class);
    }

    public void replicateToAll(String key, String value) {
        replicate("http://localhost:8082", key, value);
        replicate("http://localhost:8083", key, value);
    }

    public void replicateToNeighbors(String key, String value) {
        replicate("http://localhost:8082", key, value);
    }
}

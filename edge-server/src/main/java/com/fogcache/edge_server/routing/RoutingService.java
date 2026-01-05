package com.fogcache.edge_server.routing;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class RoutingService {

    private final RestTemplate rest = new RestTemplate();

    public List<String> getHealthyNodes() {

        String[] nodes = rest.getForObject(
                "http://localhost:8080/nodes/healthy",
                String[].class
        );

        return Arrays.asList(nodes);
    }
}

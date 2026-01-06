package com.fogcache.edge_server.routing;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoutingService {

    /**
     * Day 24: Docker-safe, static routing.
     * No env, no ports, no external dependencies.
     */
    public List<String> getHealthyNodes() {
        return List.of("self");
    }
}

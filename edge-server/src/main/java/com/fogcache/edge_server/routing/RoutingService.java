package com.fogcache.edge_server.routing;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoutingService {

    /**
     * Absolute URLs of all edge nodes in the cluster.
     * Provided by environment (local / docker / k8s).
     */
    @Value("${fogcache.cluster.nodes}")
    private List<String> clusterNodes;

    public List<String> getHealthyNodes() {
        return clusterNodes;
    }
}

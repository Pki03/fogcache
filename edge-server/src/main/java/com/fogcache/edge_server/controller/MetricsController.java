package com.fogcache.edge_server.controller;

import com.fogcache.edge_server.metrics.EdgeMetrics;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MetricsController {

    private final EdgeMetrics metrics;

    public MetricsController(EdgeMetrics metrics) {
        this.metrics = metrics;
    }

    @GetMapping("/metrics")
    public Map<String, Object> metrics() {
        metrics.recordInternal();
        return metrics.snapshot();
    }
}



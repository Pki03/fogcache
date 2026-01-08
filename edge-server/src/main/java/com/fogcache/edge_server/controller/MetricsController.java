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
        return Map.of(
                "total_requests", metrics.totalRequests.get(),
                "cache_hits", metrics.cacheHits.get(),
                "cache_misses", metrics.cacheMisses.get(),
                "origin_calls", metrics.originCalls.get(),
                "errors", metrics.errors.get(),
                "avg_latency_ms", metrics.avgLatencyMs()
        );
    }
}

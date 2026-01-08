package com.fogcache.edge_server.metrics;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class EdgeMetrics {

    public final AtomicLong totalRequests = new AtomicLong();
    public final AtomicLong cacheHits = new AtomicLong();
    public final AtomicLong cacheMisses = new AtomicLong();
    public final AtomicLong originCalls = new AtomicLong();
    public final AtomicLong errors = new AtomicLong();
    public final AtomicLong totalLatencyMs = new AtomicLong();

    public long avgLatencyMs() {
        long count = totalRequests.get();
        return count == 0 ? 0 : totalLatencyMs.get() / count;
    }

    // ✅ ADD THIS
    public Map<String, Object> snapshot() {
        return Map.of(
                "total_requests", totalRequests.get(),
                "cache_hits", cacheHits.get(),
                "cache_misses", cacheMisses.get(),
                "origin_calls", originCalls.get(),
                "errors", errors.get(),
                "avg_latency_ms", avgLatencyMs()
        );
    }
}

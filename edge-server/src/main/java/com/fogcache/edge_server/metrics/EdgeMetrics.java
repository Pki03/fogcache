package com.fogcache.edge_server.metrics;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class EdgeMetrics {

    private final AtomicLong externalRequests = new AtomicLong();
    private final AtomicLong internalRequests = new AtomicLong();

    public final AtomicLong cacheHits = new AtomicLong();
    public final AtomicLong cacheMisses = new AtomicLong();
    public final AtomicLong originCalls = new AtomicLong();
    public final AtomicLong errors = new AtomicLong();
    public final AtomicLong totalLatencyMs = new AtomicLong();

    // ---- Recording ----

    public void recordExternal() {
        externalRequests.incrementAndGet();
    }

    public void recordInternal() {
        internalRequests.incrementAndGet();
    }

    public long getExternalRequests() {
        return externalRequests.get();
    }

    public long getInternalRequests() {
        return internalRequests.get();
    }

    public long avgLatencyMs() {
        long count = externalRequests.get();
        return count == 0 ? 0 : totalLatencyMs.get() / count;
    }

    // ---- Snapshot for UI ----

    public Map<String, Object> snapshot() {
        return Map.of(
                "external_requests", getExternalRequests(),
                "internal_requests", getInternalRequests(),
                "cache_hits", cacheHits.get(),
                "cache_misses", cacheMisses.get(),
                "origin_calls", originCalls.get(),
                "errors", errors.get(),
                "avg_latency_ms", avgLatencyMs()
        );
    }
}

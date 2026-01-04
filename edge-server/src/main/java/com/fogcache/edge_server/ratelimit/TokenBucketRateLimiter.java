package com.fogcache.edge_server.ratelimit;

import java.util.concurrent.ConcurrentHashMap;

public class TokenBucketRateLimiter {

    private static class Bucket {
        double tokens;
        long lastRefillTime;

        Bucket(double tokens, long time) {
            this.tokens = tokens;
            this.lastRefillTime = time;
        }
    }

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    private final double CAPACITY = 1000;       // max requests
    private final double REFILL_RATE = 1000;    // per second

    public synchronized boolean allowRequest(String key) {
        long now = System.nanoTime();

        Bucket bucket = buckets.computeIfAbsent(key,
                k -> new Bucket(CAPACITY, now));

        double elapsedSeconds = (now - bucket.lastRefillTime) / 1e9;
        double refill = elapsedSeconds * REFILL_RATE;

        bucket.tokens = Math.min(CAPACITY, bucket.tokens + refill);
        bucket.lastRefillTime = now;

        if (bucket.tokens >= 1) {
            bucket.tokens -= 1;
            return true;
        }

        return false;
    }
}

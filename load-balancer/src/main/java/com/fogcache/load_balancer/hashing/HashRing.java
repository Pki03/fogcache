package com.fogcache.load_balancer.hashing;

import com.fogcache.load_balancer.chaos.ChaosController;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class HashRing {

    private final SortedMap<Long, String> ring = new TreeMap<>();
    private final Map<String, Boolean> health = new ConcurrentHashMap<>();

    private final ChaosController chaos;

    // ✅ Constructor Injection (correct Spring practice)
    public HashRing(ChaosController chaos) {
        this.chaos = chaos;
    }

    // ─────────────────────────────────────────────────────────────

    public void addNode(String node) {
        for (int i = 0; i < 50; i++) {
            long hash = hash(node + "#VN" + i);
            ring.put(hash, node);
        }
        health.put(node, true);
        System.out.println("Node registered: " + node);
    }

    public Set<String> getAllNodes() {
        return new HashSet<>(health.keySet());
    }

    public void markUp(String node) {
        health.put(node, true);
        System.out.println("Node UP -> " + node);
    }

    public void markDown(String node) {
        health.put(node, false);
        System.out.println("Node DOWN -> " + node);
    }

    // ─────────────────────────────────────────────────────────────

    public String getNode(String key) {

        long h = hash(key);

        // 1️⃣ Walk clockwise from hash position
        for (String node : ring.tailMap(h).values()) {
            if (isUsable(node)) return route(key, node);
        }

        // 2️⃣ Wrap around to beginning
        for (String node : ring.values()) {
            if (isUsable(node)) return route(key, node);
        }

        throw new RuntimeException("No healthy nodes available");
    }

    // ─────────────────────────────────────────────────────────────

    private boolean isUsable(String node) {

        // 💥 Chaos injection
        if (chaos.isDead(node)) {
            markDown(node);
            System.out.println("🔥 CHAOS: Injected failure -> " + node);
            return false;
        }

        return health.getOrDefault(node, false);
    }

    private String route(String key, String node) {
        System.out.println("Routing key [" + key + "] to " + node);
        return node;
    }

    // ─────────────────────────────────────────────────────────────

    private long hash(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(key.getBytes(StandardCharsets.UTF_8));
            return new BigInteger(1, bytes).longValue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

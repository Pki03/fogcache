package com.fogcache.load_balancer.hashing;

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

    public String getNode(String key) {
        long h = hash(key);

        // Search clockwise
        SortedMap<Long, String> tail = ring.tailMap(h);

        for (String node : tail.values()) {
            if (health.getOrDefault(node, false)) {
                log(key, node);
                return node;
            }
        }

        // Wrap around
        for (String node : ring.values()) {
            if (health.getOrDefault(node, false)) {
                log(key, node);
                return node;
            }
        }

        throw new RuntimeException("No healthy nodes available");
    }

    private long hash(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(key.getBytes(StandardCharsets.UTF_8));
            return new BigInteger(1, bytes).longValue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void log(String key, String node) {
        System.out.println("Routing key [" + key + "] to " + node);
    }
}

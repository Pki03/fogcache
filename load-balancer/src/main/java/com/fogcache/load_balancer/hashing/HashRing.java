package com.fogcache.load_balancer.hashing;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.*;
import org.springframework.stereotype.Component;

@Component
public class HashRing {

    private final SortedMap<Long, String> ring = new TreeMap<>();
    private final Map<String, Boolean> health = new HashMap<>();

    private long hash(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(key.getBytes());
            return new BigInteger(1, bytes).longValue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addNode(String node) {
        for (int i = 0; i < 50; i++) {
            String virtualNode = node + "#VN" + i;
            long h = hash(virtualNode);
            ring.put(h, node);
        }
        health.put(node, true);
    }

    public void markDown(String node) {
        health.put(node, false);
        System.out.println("Node DOWN -> " + node);
    }

    public void markUp(String node) {
        health.put(node, true);
        System.out.println("Node UP -> " + node);
    }

    public Set<String> getAllNodes() {
        return new HashSet<>(health.keySet());
    }

    public String getNode(String key) {

        long h = hash(key);

        SortedMap<Long, String> tail = ring.tailMap(h);
        long chosenHash = tail.isEmpty() ? ring.firstKey() : tail.firstKey();
        String chosenNode = ring.get(chosenHash);

        // Skip dead nodes
        while (!health.getOrDefault(chosenNode, false)) {
            ring.remove(chosenHash);

            if (ring.isEmpty()) {
                throw new RuntimeException("No healthy nodes available");
            }

            chosenHash = ring.firstKey();
            chosenNode = ring.get(chosenHash);
        }

        System.out.println("Routing key [" + key + "] to " + chosenNode);
        return chosenNode;
    }
}

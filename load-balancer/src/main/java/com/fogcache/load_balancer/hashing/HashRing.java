package com.fogcache.load_balancer.hashing;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.SortedMap;
import java.util.TreeMap;

public class HashRing {

    private final SortedMap<Long, String> ring = new TreeMap<>();

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
            System.out.println("Added node: " + virtualNode + " @ " + h);
        }
    }

    public String getNode(String key) {
        long h = hash(key);
        System.out.println("Key hash: " + h);

        SortedMap<Long, String> tail = ring.tailMap(h);

        long chosenHash = tail.isEmpty() ? ring.firstKey() : tail.firstKey();
        String chosenNode = ring.get(chosenHash);

        System.out.println("Routing to: " + chosenNode);
        return chosenNode;
    }
}

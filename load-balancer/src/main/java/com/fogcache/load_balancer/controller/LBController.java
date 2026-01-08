package com.fogcache.load_balancer.controller;

import com.fogcache.load_balancer.hashing.HashRing;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
public class LBController {

    private final HashRing hashRing;
    private final RestTemplate rest = new RestTemplate();

    public LBController(HashRing hashRing) {
        this.hashRing = hashRing;
    }

    @GetMapping("/content")
    public String route(@RequestParam String id) {

        System.out.println("🔵 [LB] Incoming request id=" + id);

        // 🔑 THIS IS THE KEY LINE — consistent hashing
        String node = hashRing.getNode(id);

        System.out.println("🟢 [LB] HashRing selected node=" + node);

        String response = rest.getForObject(
                node + "/content?id=" + id,
                String.class
        );

        System.out.println("🟣 [LB] Response received from " + node);

        return response;
    }
}

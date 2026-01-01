package com.fogcache.load_balancer.controller;

import com.fogcache.load_balancer.hashing.HashRing;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class LBController {

    private final HashRing ring = new HashRing();
    private final RestTemplate restTemplate = new RestTemplate();

    public LBController() {
        ring.addNode("http://localhost:8082");
        ring.addNode("http://localhost:8083");
    }

    @GetMapping("/content")
    public String route(@RequestParam String id) {
        System.out.println("\nIncoming request for key: " + id);

        String edge = ring.getNode(id);
        return restTemplate.getForObject(edge + "/content?id=" + id, String.class);
    }
}

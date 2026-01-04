package com.fogcache.load_balancer.chaos;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chaos")
public class ChaosApi {

    private final ChaosController chaos;

    public ChaosApi(ChaosController chaos) {
        this.chaos = chaos;
    }

    @PostMapping("/kill")
    public String kill(@RequestParam String node) {
        chaos.kill(node);
        return "Killed " + node;
    }

    @PostMapping("/revive")
    public String revive(@RequestParam String node) {
        chaos.revive(node);
        return "Revived " + node;
    }
}

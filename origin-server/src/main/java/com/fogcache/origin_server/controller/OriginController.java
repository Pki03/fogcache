package com.fogcache.origin_server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OriginController {

    @GetMapping("/content")
    public String getContent(@RequestParam String id) throws InterruptedException {

        // Simulate slow database
        Thread.sleep(100);

        return "DATA_FOR_" + id;
    }
}

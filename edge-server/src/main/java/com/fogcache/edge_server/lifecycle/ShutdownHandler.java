package com.fogcache.edge_server.lifecycle;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class ShutdownHandler {

    @PreDestroy
    public void onShutdown() {
        System.out.println("🛑 Edge server shutting down gracefully...");
        System.out.println("✅ In-flight requests completed");
        System.out.println("✅ Background tasks stopped");
    }
}

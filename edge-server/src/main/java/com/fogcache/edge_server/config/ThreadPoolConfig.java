package com.fogcache.edge_server.config;

import org.springframework.context.annotation.*;
import java.util.concurrent.*;

@Configuration
public class ThreadPoolConfig {

    @Bean("requestPool")
    public ExecutorService requestPool() {
        return new ThreadPoolExecutor(
                20, 50,
                60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(500)
        );
    }

    @Bean("originPool")
    public ExecutorService originPool() {
        return Executors.newFixedThreadPool(10);
    }

    @Bean("replicationPool")
    public ExecutorService replicationPool() {
        return Executors.newFixedThreadPool(5);
    }
}

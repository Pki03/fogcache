package com.fogcache.edge_server.logging;

import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class RequestLogger {

    private final List<RequestLog> logs = new CopyOnWriteArrayList<>();

    public void log(String key,
                    String node,
                    boolean hit,
                    long latency,
                    String value) {

        logs.add(new RequestLog(
                key,
                node,
                hit,
                latency,
                System.currentTimeMillis(),
                value
        ));
    }


    public List<RequestLog> getLogs() {
        return logs;
    }
}

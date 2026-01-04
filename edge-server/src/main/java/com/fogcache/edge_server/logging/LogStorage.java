package com.fogcache.edge_server.logging;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class LogStorage {

    private final List<RequestLog> logs = new CopyOnWriteArrayList<>();

    public void add(RequestLog log) {
        logs.add(log);
    }

    public List<RequestLog> getAll() {
        return new ArrayList<>(logs);
    }
}

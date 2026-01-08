package com.fogcache.edge_server.replication;

public class DecisionState {

    private volatile String lastClass;
    private volatile long lastUpdated;

    public DecisionState(String lastClass, long lastUpdated) {
        this.lastClass = lastClass;
        this.lastUpdated = lastUpdated;
    }

    public String getLastClass() {
        return lastClass;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void update(String newClass, long now) {
        this.lastClass = newClass;
        this.lastUpdated = now;
    }

    public void touch(long now) {
        this.lastUpdated = now;
    }
}

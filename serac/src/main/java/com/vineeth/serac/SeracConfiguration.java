package com.vineeth.serac;

public class SeracConfiguration {
    private Long gossipInterval;
    private Long healthyThreshold;
    private int gossipServerPort;

    public Long getGossipInterval() {
        return gossipInterval;
    }

    public void setGossipInterval(Long gossipInterval) {
        this.gossipInterval = gossipInterval;
    }

    public Long getHealthyThreshold() {
        return healthyThreshold;
    }

    public void setHealthyThreshold(Long healthyThreshold) {
        this.healthyThreshold = healthyThreshold;
    }

    public int getGossipServerPort() {
        return gossipServerPort;
    }

    public void setGossipServerPort(int gossipServerPort) {
        this.gossipServerPort = gossipServerPort;
    }
}

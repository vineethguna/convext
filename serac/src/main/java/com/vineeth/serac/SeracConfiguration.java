package com.vineeth.serac;

public class SeracConfiguration {
    private Long gossipInterval;
    private Long healthyThreshold;
    private int gossipServerPort;
    private String peerHost;
    private int peerPort;
    private String nodeId;

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

    public String getPeerHost() {
        return peerHost;
    }

    public void setPeerHost(String peerHost) {
        this.peerHost = peerHost;
    }

    public int getPeerPort() {
        return peerPort;
    }

    public void setPeerPort(int peerPort) {
        this.peerPort = peerPort;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
}

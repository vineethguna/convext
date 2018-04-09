package com.vineeth.serac.store;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HeartBeatStore {
    private Map<String, Long> heartBeatStore;
    private NodeStore nodeStore;

    public HeartBeatStore(NodeStore nodeStore) {
        this.nodeStore = nodeStore;
        heartBeatStore = new ConcurrentHashMap<>();
    }


    public void addNode(String nodeId) {
        if(nodeStore.containsNode(nodeId)) {
            heartBeatStore.putIfAbsent(nodeId, 0L);
        }
    }

    public void updateHeartBeatForNode(String nodeId, Long timeStamp) {
        heartBeatStore.compute(nodeId, (key, value) -> timeStamp);
    }

    public void deleteNode(String nodeId) {
        heartBeatStore.remove(nodeId);
    }

    public Long getHeartBeatForNode(String nodeId) {
        return heartBeatStore.compute(nodeId, (key, value) -> value);
    }
}

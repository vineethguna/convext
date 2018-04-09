package com.vineeth.serac.store;


import com.vineeth.serac.gossip.GossipNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NodeStore {
    private GossipNode currentNode;
    private Map<String, GossipNode> nodeStore;

    public NodeStore(GossipNode currentNode) {
        nodeStore = new ConcurrentHashMap<>();
        this.currentNode = currentNode;
    }

    public GossipNode getNodeById(String nodeId) {
        return nodeStore.compute(nodeId, (key, value) -> value);
    }

    public void addNode(String nodeId, GossipNode node) {
        nodeStore.putIfAbsent(nodeId, node);
    }

    public boolean containsNode(String nodeId) {
        return nodeStore.containsKey(nodeId);
    }

    public GossipNode getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(GossipNode currentNode) {
        this.currentNode = currentNode;
    }

    public List<String> getAllNodes() {
        return new ArrayList<>(nodeStore.keySet());
    }
}

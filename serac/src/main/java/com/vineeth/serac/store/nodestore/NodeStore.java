package com.vineeth.serac.store.nodestore;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NodeStore {
    private Node currentNode;
    private Map<String, Node> nodeStore;

    public NodeStore(Node currentNode) {
        nodeStore = new ConcurrentHashMap<>();
        this.currentNode = currentNode;
    }

    public Node getNodeById(String nodeId) {
        return nodeStore.compute(nodeId, (key, value) -> value);
    }

    public void addNode(String nodeId, Node node) {
        nodeStore.putIfAbsent(nodeId, node);
    }

    public boolean containsNode(String nodeId) {
        return nodeStore.containsKey(nodeId);
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(Node currentNode) {
        this.currentNode = currentNode;
    }

    public List<String> getAllNodeIds() {
        return new ArrayList<>(nodeStore.keySet());
    }
}

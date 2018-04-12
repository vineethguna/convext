package com.vineeth.serac.store.suspectstore;

import com.vineeth.serac.store.nodestore.NodeStore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SuspectStore {
    private Map<String, SuspectRow> suspectStore;
    private NodeStore nodeStore;

    public SuspectStore(NodeStore nodeStore) {
        this.nodeStore = nodeStore;
        suspectStore = new ConcurrentHashMap<>();
    }

    public void addNode(String nodeId) {
        if(nodeStore.containsNode(nodeId) && !suspectStore.containsKey(nodeId)) {
            addNewNodeColumnToAllNodeRows(nodeId);
            addNewNodeRow(nodeId);
        }
    }

    private void addNewNodeColumnToAllNodeRows(String nodeId) {
        for(String nodeIdInSuspectStore: suspectStore.keySet()) {
            SuspectRow row = suspectStore.get(nodeIdInSuspectStore);
            row.addNewNode(nodeId);
        }
    }

    private void addNewNodeRow(String nodeId) {
        SuspectRow suspectRow = new SuspectRow();
        suspectRow.addNewNode(nodeId);
        for(String nodeIdInSuspectStore: suspectStore.keySet()) {
            suspectRow.addNewNode(nodeIdInSuspectStore);
        }

        suspectStore.putIfAbsent(nodeId, suspectRow);
    }

    public SuspectRow getSuspectRowForNode(String nodeId) {
        return suspectStore.compute(nodeId, (key, value) -> value);
    }

    public int getSuspectCountForNodeId(String nodeId) {
        int suspectCount = 0;
        for(SuspectRow suspectRow: suspectStore.values()) {
            if(suspectRow.isNodeSuspected(nodeId)) {
                suspectCount++;
            }
        }
        return suspectCount;
    }
}

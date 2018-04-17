package com.vineeth.serac.store.suspectstore;

import java.util.HashMap;
import java.util.Map;

public class SuspectRow {
    private Map<String, Boolean> row;
    private Long lastUpdatedTimeStamp;

    public SuspectRow() {
        row = new HashMap<>();
        lastUpdatedTimeStamp = 0L;
    }

    public Long getLastUpdatedTimeStamp() {
        return lastUpdatedTimeStamp;
    }

    public void setLastUpdatedTimeStamp(Long lastUpdatedTimeStamp) {
        this.lastUpdatedTimeStamp = lastUpdatedTimeStamp;
    }

    public Map<String, Boolean> getRow() {
        return row;
    }

    public void setRow(Map<String, Boolean> row) {
        this.row = row;
    }

    public void addNewNode(String nodeId) {
        row.putIfAbsent(nodeId, false);
    }

    public void updateStateForNode(String nodeId, Boolean newState) {
        row.compute(nodeId, (key, value) -> newState);
        setLastUpdatedTimeStamp(System.currentTimeMillis());
    }

    public boolean isNodeSuspected(String nodeId) {
        return row.compute(nodeId, (key, value) -> value);
    }
}

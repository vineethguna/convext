package com.vineeth.serac.messages;


import com.vineeth.serac.store.nodestore.Node;
import com.vineeth.serac.store.suspectstore.SuspectRow;

import java.util.Map;

public class GossipMessage extends Message {
    private String nodeId;
    private Map<String, Node> nodeData;
    private Map<String, Long> heartBeatData;
    private Map<String, SuspectRow> suspectData;


    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public Map<String, Long> getHeartBeatData() {
        return heartBeatData;
    }

    public void setHeartBeatData(Map<String, Long> heartBeatData) {
        this.heartBeatData = heartBeatData;
    }

    public Map<String, SuspectRow> getSuspectData() {
        return suspectData;
    }

    public void setSuspectData(Map<String, SuspectRow> suspectData) {
        this.suspectData = suspectData;
    }

    public Map<String, Node> getNodeData() {
        return nodeData;
    }

    public void setNodeData(Map<String, Node> nodeData) {
        this.nodeData = nodeData;
    }
}

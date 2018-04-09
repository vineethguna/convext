package com.vineeth.serac.manager.processors;


import com.vineeth.serac.gossip.GossipNode;
import com.vineeth.serac.messages.GossipMessage;
import com.vineeth.serac.messages.Message;
import com.vineeth.serac.store.HeartBeatStore;
import com.vineeth.serac.store.NodeStore;
import com.vineeth.serac.store.suspectstore.SuspectRow;
import com.vineeth.serac.store.suspectstore.SuspectStore;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class MessageProcessor implements IProcessor {
    private NodeStore nodeStore;
    private HeartBeatStore heartBeatStore;
    private SuspectStore suspectStore;

    public MessageProcessor(NodeStore nodeStore, HeartBeatStore heartBeatStore, SuspectStore suspectStore) {
        this.nodeStore = nodeStore;
        this.heartBeatStore = heartBeatStore;
        this.suspectStore = suspectStore;
    }

    @Override
    public CompletionStage<ProcessorContext> process(ProcessorContext context) {
        CompletionStage<ProcessorContext> stage = new CompletableFuture<>();
        processMessages(context.getMessages());
        return stage;
    }

    private void processMessages(List<Message> messages) {
        for(Message message: messages) {
            if(message instanceof GossipMessage) {
                processGossipMessage((GossipMessage) message);
            }
        }
    }

    private void processGossipMessage(GossipMessage gossipMessage) {
        String senderNodeId = gossipMessage.getNodeId();
        handleNodeData(gossipMessage.getNodeData());
        handleHeartBeatData(gossipMessage.getHeartBeatData());
        handleSuspectData(gossipMessage.getSuspectData());
        heartBeatStore.updateHeartBeatForNode(senderNodeId, System.currentTimeMillis());
    }

    private void handleNodeData(Map<String, GossipNode> nodeData) {
        for(String nodeId: nodeData.keySet()) {
            if(!nodeStore.containsNode(nodeId)) {
                GossipNode newNode = nodeData.get(nodeId);
                nodeStore.addNode(nodeId, newNode);
                heartBeatStore.addNode(nodeId);
                suspectStore.addNode(nodeId);
            }
        }
    }

    private void handleHeartBeatData(Map<String, Long> heartBeatData) {
        for(String nodeIdFromHeartBeat: heartBeatData.keySet()) {
            Long heartBeatFromStore = heartBeatStore.getHeartBeatForNode(nodeIdFromHeartBeat);
            Long heartBeatFromGossipMessage = heartBeatData.get(nodeIdFromHeartBeat);
            if(heartBeatFromGossipMessage > heartBeatFromStore) {
                heartBeatStore.updateHeartBeatForNode(nodeIdFromHeartBeat, heartBeatFromGossipMessage);
            }
        }
    }

    private void handleSuspectData(Map<String, SuspectRow> suspectData) {
        suspectData.remove(nodeStore.getCurrentNode().getId());
        for(String nodeIdFromSuspectData: suspectData.keySet()) {
            SuspectRow suspectRowFromSuspectStore = suspectStore.getSuspectRowForNode(nodeIdFromSuspectData);
            SuspectRow suspectRowFromGossipMessage = suspectData.get(nodeIdFromSuspectData);
            if(suspectRowFromGossipMessage.getLastUpdatedTimeStamp() >
                    suspectRowFromSuspectStore.getLastUpdatedTimeStamp()) {
                suspectRowFromSuspectStore.setLastUpdatedTimeStamp(
                        suspectRowFromGossipMessage.getLastUpdatedTimeStamp());
                suspectRowFromSuspectStore.setRow(suspectRowFromGossipMessage.getRow());
            }
        }
    }
}

package com.vineeth.serac.manager.pipeline.processors;


import com.vineeth.serac.manager.pipeline.IProcessor;
import com.vineeth.serac.manager.pipeline.ProcessorContext;
import com.vineeth.serac.messages.GossipMessage;
import com.vineeth.serac.messages.Message;
import com.vineeth.serac.store.HeartBeatStore;
import com.vineeth.serac.store.MessageStore;
import com.vineeth.serac.store.nodestore.Node;
import com.vineeth.serac.store.nodestore.NodeStore;
import com.vineeth.serac.store.suspectstore.SuspectRow;
import com.vineeth.serac.store.suspectstore.SuspectStore;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MessageProcessor implements IProcessor {
    private MessageStore messageStore;
    private NodeStore nodeStore;
    private HeartBeatStore heartBeatStore;
    private SuspectStore suspectStore;

    public MessageProcessor(MessageStore messageStore, NodeStore nodeStore,
                            HeartBeatStore heartBeatStore, SuspectStore suspectStore) {
        this.messageStore = messageStore;
        this.nodeStore = nodeStore;
        this.heartBeatStore = heartBeatStore;
        this.suspectStore = suspectStore;
    }

    @Override
    public CompletableFuture<ProcessorContext> process(ProcessorContext context) {
        CompletableFuture<ProcessorContext> future = new CompletableFuture<>();
        processMessages(messageStore.clearAndGetAllMessages());
        future.complete(context);
        return future;
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

    private void handleNodeData(Map<String, Node> nodeData) {
        for(String nodeId: nodeData.keySet()) {
            if(!nodeStore.containsNode(nodeId)) {
                Node newNode = nodeData.get(nodeId);
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

package com.vineeth.serac.manager.pipeline.processors;


import com.vineeth.serac.manager.pipeline.IProcessor;
import com.vineeth.serac.manager.pipeline.ProcessorContext;
import com.vineeth.serac.messages.GossipMessage;
import com.vineeth.serac.messages.MessageType;
import com.vineeth.serac.store.HeartBeatStore;
import com.vineeth.serac.store.nodestore.Node;
import com.vineeth.serac.store.nodestore.NodeStore;
import com.vineeth.serac.store.suspectstore.SuspectRow;
import com.vineeth.serac.store.suspectstore.SuspectStore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CreateGossipMessageProcessor implements IProcessor {
    private NodeStore nodeStore;
    private HeartBeatStore heartBeatStore;
    private SuspectStore suspectStore;

    public CreateGossipMessageProcessor(NodeStore nodeStore, HeartBeatStore heartBeatStore, SuspectStore suspectStore) {
        this.nodeStore = nodeStore;
        this.heartBeatStore = heartBeatStore;
        this.suspectStore = suspectStore;
    }

    @Override
    public CompletableFuture<ProcessorContext> process(ProcessorContext context) {
        CompletableFuture<ProcessorContext> future = new CompletableFuture<>();
        GossipMessage gossipMessage = createGossipMessage();
        context.setGossipMessageToSend(gossipMessage);
        future.complete(context);
        return future;
    }

    private GossipMessage createGossipMessage() {
        GossipMessage gossipMessage = new GossipMessage();
        List<String> nodeIds = nodeStore.getAllNodeIds();
        gossipMessage.setType(MessageType.GOSSIP);
        gossipMessage.setNodeId(nodeStore.getCurrentNode().getId());
        gossipMessage.setNodeData(createNodeData(nodeIds));
        gossipMessage.setHeartBeatData(createHeartBeatData(nodeIds));
        gossipMessage.setSuspectData(createSuspectData(nodeIds));
        return gossipMessage;
    }

    private Map<String, Node> createNodeData(List<String> nodeIds) {
        Map<String, Node> nodeData = new HashMap<>();
        for(String nodeId : nodeIds) {
            nodeData.put(nodeId, nodeStore.getNodeById(nodeId));
        }
        return nodeData;
    }

    private Map<String, Long> createHeartBeatData(List<String> nodeIds) {
        Map<String, Long> heartBeatData = new HashMap<>();
        for(String nodeId : nodeIds) {
            heartBeatData.put(nodeId, heartBeatStore.getHeartBeatForNode(nodeId));
        }
        return heartBeatData;
    }

    private Map<String, SuspectRow> createSuspectData(List<String> nodeIds) {
        Map<String, SuspectRow> suspectData = new HashMap<>();
        for(String nodeId : nodeIds) {
            suspectData.put(nodeId, suspectStore.getSuspectRowForNode(nodeId));
        }
        return suspectData;
    }

}

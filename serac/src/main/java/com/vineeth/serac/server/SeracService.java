package com.vineeth.serac.server;

import com.vineeth.serac.manager.SeracManager;
import com.vineeth.serac.messages.GossipMessage;
import com.vineeth.serac.messages.Message;
import com.vineeth.serac.messages.MessageType;
import com.vineeth.serac.rpc.SeracGrpc;
import com.vineeth.serac.rpc.SeracProto;
import com.vineeth.serac.rpc.SeracProto.Response;
import com.vineeth.serac.store.nodestore.Node;
import com.vineeth.serac.store.suspectstore.SuspectRow;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SeracService extends SeracGrpc.SeracImplBase {
    private static final Logger logger = LoggerFactory.getLogger(SeracService.class);

    private SeracManager seracManager;

    public SeracService(SeracManager seracManager) {
        this.seracManager = seracManager;
    }

    @Override
    public void processMessage(SeracProto.Message rpcMessage,
                                        StreamObserver<Response> responseObserver) {
        logger.info("Received message through rpc");
        try {
            Message message = convertRpcMessageToMessage(rpcMessage);
            seracManager.handleMessage(message);
            responseObserver.onNext(Response.newBuilder().setErrorInfo("").setSuccess(true).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("RPC Message processing failed", e);
        }

    }

    private Message convertRpcMessageToMessage(SeracProto.Message message) throws Exception {
        switch(message.getType()) {
            case GOSSIP:
                return convertRpcGossipMessageToGossipMessage(message.getGossipMessage());
            default:
                throw new Exception("Unidentified message type");
        }
    }

    private GossipMessage convertRpcGossipMessageToGossipMessage(SeracProto.GossipMessage rpcGossipMessage) {
        GossipMessage gossipMessage = new GossipMessage();
        gossipMessage.setType(MessageType.GOSSIP);
        gossipMessage.setHeartBeatData(rpcGossipMessage.getHeartBeatDataMap());
        gossipMessage.setNodeData(getNodeDataFromRpcNodeData(rpcGossipMessage.getNodeDataMap()));
        gossipMessage.setNodeId(rpcGossipMessage.getNodeId());
        gossipMessage.setSuspectData(getSuspectDataFromRpcSuspectData(rpcGossipMessage.getSuspectDataMap()));
        return gossipMessage;
    }

    private Map<String, Node> getNodeDataFromRpcNodeData(Map<String, SeracProto.GossipNode> rpcNodeData) {
        Map<String, Node> nodeData = new ConcurrentHashMap<>();
        for(String id: rpcNodeData.keySet()) {
            SeracProto.GossipNode gossipNodeFromRpc = rpcNodeData.get(id);
            Node node = new Node();
            node.setId(gossipNodeFromRpc.getId());
            node.setHost(gossipNodeFromRpc.getHostname());
            node.setPort(gossipNodeFromRpc.getPort());
            node.setHealthy(gossipNodeFromRpc.getIsHealthy());
            nodeData.putIfAbsent(id, node);
        }
        return nodeData;
    }

    private Map<String, SuspectRow> getSuspectDataFromRpcSuspectData(
            Map<String, SeracProto.SuspectRow> rpcSuspectData) {
        Map<String, SuspectRow> suspectData = new ConcurrentHashMap<>();
        for(String id: rpcSuspectData.keySet()) {
            SeracProto.SuspectRow suspectRowFromRpc = rpcSuspectData.get(id);
            SuspectRow suspectRow = new SuspectRow();
            suspectRow.setRow(suspectRowFromRpc.getRowMap());
            suspectRow.setLastUpdatedTimeStamp(suspectRowFromRpc.getLastUpdatedTimeStamp());
            suspectData.putIfAbsent(id, suspectRow);
        }
        return suspectData;
    }
}

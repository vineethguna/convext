package com.vineeth.serac.rpc.client;

import com.vineeth.serac.messages.GossipMessage;
import com.vineeth.serac.messages.Message;
import com.vineeth.serac.rpc.SeracGrpc;
import com.vineeth.serac.rpc.SeracGrpc.SeracBlockingStub;
import com.vineeth.serac.rpc.SeracGrpc.SeracStub;
import com.vineeth.serac.rpc.SeracProto;
import com.vineeth.serac.store.nodestore.Node;
import com.vineeth.serac.store.suspectstore.SuspectRow;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SeracRpcClient {
    private static final Logger logger = LoggerFactory.getLogger(SeracRpcClient.class);

    private final ManagedChannel channel;
    private final SeracBlockingStub blockingStub;
    private final SeracStub asyncStub;

    public SeracRpcClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext());
    }

    private SeracRpcClient(ManagedChannelBuilder<?> channelBuilder) {
        channel = channelBuilder.build();
        blockingStub = SeracGrpc.newBlockingStub(channel);
        asyncStub = SeracGrpc.newStub(channel);
    }

    public void shutdown() {
        channel.shutdown();
    }

    public void callProcessMessage(Message message) throws Exception {
        SeracProto.Message.Builder rpcMessageBuilder = SeracProto.Message.newBuilder();
        if(message instanceof GossipMessage) {
            rpcMessageBuilder.setType(SeracProto.Message.Type.GOSSIP);
            rpcMessageBuilder.setGossipMessage(constructRpcGossipMessage((GossipMessage) message));
        }
        SeracProto.Response response = blockingStub.processMessage(rpcMessageBuilder.build());
        if(!response.getSuccess()) {
            throw new Exception("Call Process Message not successful");
        }
    }

    private SeracProto.GossipMessage constructRpcGossipMessage(GossipMessage gossipMessage) {
        SeracProto.GossipMessage.Builder rpcGossipMessageBuilder = SeracProto.GossipMessage.newBuilder();
        rpcGossipMessageBuilder.setNodeId(gossipMessage.getNodeId());
        rpcGossipMessageBuilder.putAllHeartBeatData(gossipMessage.getHeartBeatData());
        rpcGossipMessageBuilder.putAllNodeData(constructRpcNodeData(gossipMessage));
        rpcGossipMessageBuilder.putAllSuspectData(constructRpcSuspectData(gossipMessage));
        return rpcGossipMessageBuilder.build();
    }

    private Map<String, SeracProto.GossipNode> constructRpcNodeData(GossipMessage gossipMessage) {
        Map<String, SeracProto.GossipNode> rpcNodeData = new HashMap<>();
        Map<String, Node> nodeData = gossipMessage.getNodeData();
        for(String nodeId : nodeData.keySet()) {
            Node node = nodeData.get(nodeId);
            SeracProto.GossipNode.Builder rpcGossipNodeBuilder = SeracProto.GossipNode.newBuilder();
            rpcGossipNodeBuilder.setPort(node.getPort());
            rpcGossipNodeBuilder.setId(node.getId());
            rpcGossipNodeBuilder.setHostname(node.getHost());
            rpcGossipNodeBuilder.setIsHealthy(node.isHealthy());
            rpcNodeData.put(nodeId, rpcGossipNodeBuilder.build());
        }
        return rpcNodeData;
    }

    private Map<String, SeracProto.SuspectRow> constructRpcSuspectData(GossipMessage gossipMessage) {
        Map<String, SeracProto.SuspectRow> rpcSuspectData = new HashMap<>();
        Map<String, SuspectRow> suspectData = gossipMessage.getSuspectData();
        for(String nodeId : suspectData.keySet()) {
            SuspectRow suspectRow = suspectData.get(nodeId);
            SeracProto.SuspectRow.Builder rpcSuspectRowBuilder = SeracProto.SuspectRow.newBuilder();
            rpcSuspectRowBuilder.setLastUpdatedTimeStamp(suspectRow.getLastUpdatedTimeStamp());
            rpcSuspectRowBuilder.putAllRow(suspectRow.getRow());
            rpcSuspectData.put(nodeId, rpcSuspectRowBuilder.build());
        }
        return rpcSuspectData;
    }
}

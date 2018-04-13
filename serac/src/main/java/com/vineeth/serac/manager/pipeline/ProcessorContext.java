package com.vineeth.serac.manager.pipeline;

import com.vineeth.serac.messages.GossipMessage;
import com.vineeth.serac.store.nodestore.Node;

import java.util.List;

public class ProcessorContext {
    private GossipMessage gossipMessageToSend;
    private List<Node> nodesListToSend;


    public GossipMessage getGossipMessageToSend() {
        return gossipMessageToSend;
    }

    public void setGossipMessageToSend(GossipMessage gossipMessageToSend) {
        this.gossipMessageToSend = gossipMessageToSend;
    }

    public List<Node> getNodesListToSend() {
        return nodesListToSend;
    }

    public void setNodesListToSend(List<Node> nodesListToSend) {
        this.nodesListToSend = nodesListToSend;
    }
}

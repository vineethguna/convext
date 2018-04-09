package com.vineeth.serac.gossip;

import com.vineeth.serac.messages.GossipMessage;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

public class GossipManager {
    private ScheduledExecutorService gossipEvaluationExecutorService;

    public GossipManager() {

    }

    public void handleMessage(GossipMessage message) {

    }

    public void evaluate() {

    }

    private List<GossipNode> evaluateSuspectNodes() {
        return null;
    }

    private List<GossipNode> evaluateFailedNodes() {
        return null;
    }

    private List<GossipNode> evaluateRecoveredNodes() {
        return null;
    }

    private void broadcast() {

    }
}

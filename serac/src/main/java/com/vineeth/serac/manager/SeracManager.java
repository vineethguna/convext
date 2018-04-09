package com.vineeth.serac.manager;

import com.vineeth.serac.SeracConfiguration;
import com.vineeth.serac.gossip.GossipNode;
import com.vineeth.serac.messages.Message;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SeracManager {
    private ScheduledExecutorService gossipEvaluationExecutorService;
    private SeracConfiguration configuration;


    public SeracManager(SeracConfiguration configuration) {
        this.configuration = configuration;
        gossipEvaluationExecutorService = Executors.newScheduledThreadPool(2);
    }

    public void handleMessage(Message message) {

    }

    public void startGossipEvaluation() {
        Long gossipInterval = 1000L;
        gossipEvaluationExecutorService.scheduleAtFixedRate(this::evaluate, gossipInterval, gossipInterval,
                TimeUnit.MILLISECONDS);
    }

    private void evaluate() {

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

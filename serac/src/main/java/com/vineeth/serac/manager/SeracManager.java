package com.vineeth.serac.manager;

import com.vineeth.serac.SeracConfiguration;
import com.vineeth.serac.manager.pipeline.Pipeline;
import com.vineeth.serac.manager.pipeline.processors.*;
import com.vineeth.serac.messages.Message;
import com.vineeth.serac.store.HeartBeatStore;
import com.vineeth.serac.store.MessageStore;
import com.vineeth.serac.store.nodestore.NodeStore;
import com.vineeth.serac.store.suspectstore.SuspectStore;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SeracManager {
    private ScheduledExecutorService gossipEvaluationExecutorService;
    private SeracConfiguration configuration;

    private MessageStore messageStore;
    private NodeStore nodeStore;
    private HeartBeatStore heartBeatStore;
    private SuspectStore suspectStore;
    private Pipeline pipeline;


    public SeracManager(MessageStore messageStore, NodeStore nodeStore,
                        HeartBeatStore heartBeatStore, SuspectStore suspectStore,
                        SeracConfiguration configuration) {
        this.messageStore = messageStore;
        this.nodeStore = nodeStore;
        this.heartBeatStore = heartBeatStore;
        this.suspectStore = suspectStore;
        this.configuration = configuration;
        gossipEvaluationExecutorService = Executors.newScheduledThreadPool(1);
        initializePipeline();
    }

    private void initializePipeline() {
        pipeline = new Pipeline();
        pipeline.add(new MessageProcessor(messageStore, nodeStore, heartBeatStore, suspectStore));
        pipeline.add(new EvaluateSuspectMatrixProcessor(nodeStore, heartBeatStore, suspectStore,
                configuration.getGossipInterval(), configuration.getHealthyThreshold()));
        pipeline.add(new EvaluateFailureNodesProcessor(nodeStore, suspectStore, 0.5f));
        pipeline.add(new EvaluateRecoveredNodesProcessor(nodeStore, suspectStore, 0.5f));
        pipeline.add(new CreateGossipMessageProcessor(nodeStore, heartBeatStore, suspectStore));
        pipeline.add(new CreateNodeListProcessor(nodeStore));
        pipeline.add(new SendGossipMessageRpcProcessor());
    }

    public void handleMessage(Message message) {
        messageStore.add(message);
    }

    public void startPipeline() {
        Long gossipInterval = configuration.getGossipInterval();
        gossipEvaluationExecutorService.scheduleAtFixedRate(pipeline::start, gossipInterval, gossipInterval,
                TimeUnit.MILLISECONDS);
    }
}

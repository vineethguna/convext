package com.vineeth.serac.manager.pipeline.processors;

import com.vineeth.serac.manager.pipeline.IProcessor;
import com.vineeth.serac.manager.pipeline.ProcessorContext;
import com.vineeth.serac.store.HeartBeatStore;
import com.vineeth.serac.store.nodestore.NodeStore;
import com.vineeth.serac.store.suspectstore.SuspectRow;
import com.vineeth.serac.store.suspectstore.SuspectStore;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EvaluateSuspectMatrixProcessor implements IProcessor {
    private NodeStore nodeStore;
    private HeartBeatStore heartBeatStore;
    private SuspectStore suspectStore;
    private Long gossipInterval;
    private Long healthyThreshold;


    public EvaluateSuspectMatrixProcessor(NodeStore nodeStore, HeartBeatStore heartBeatStore,
                                          SuspectStore suspectStore, Long gossipInterval,
                                          Long healthyThreshold) {
        this.nodeStore = nodeStore;
        this.heartBeatStore = heartBeatStore;
        this.suspectStore = suspectStore;
        this.gossipInterval = gossipInterval;
        this.healthyThreshold = healthyThreshold;
    }

    @Override
    public CompletableFuture<ProcessorContext> process(ProcessorContext context) {
        CompletableFuture<ProcessorContext>  future = new CompletableFuture<>();
        evaluateSuspectMatrixForCurrentNode();
        future.complete(context);
        return future;
    }

    private void evaluateSuspectMatrixForCurrentNode() {
        List<String> nodeIds = nodeStore.getAllNodeIds();
        nodeIds.remove(nodeStore.getCurrentNode().getId());
        SuspectRow suspectRowForCurrentNode = suspectStore.getSuspectRowForNode(nodeStore.getCurrentNode().getId());
        for(String nodeId: nodeIds) {
            Long heartBeatTimeStamp = heartBeatStore.getHeartBeatForNode(nodeId);
            Long currentTimeStamp = System.currentTimeMillis();
            if((currentTimeStamp - heartBeatTimeStamp) > healthyThreshold * gossipInterval) {
                suspectRowForCurrentNode.updateStateForNode(nodeId, true);
            }
        }
    }
}

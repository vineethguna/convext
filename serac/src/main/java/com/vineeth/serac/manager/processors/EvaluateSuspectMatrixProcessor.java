package com.vineeth.serac.manager.processors;

import com.vineeth.serac.store.HeartBeatStore;
import com.vineeth.serac.store.NodeStore;
import com.vineeth.serac.store.suspectstore.SuspectRow;
import com.vineeth.serac.store.suspectstore.SuspectStore;

import java.util.List;
import java.util.concurrent.CompletionStage;

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
    public CompletionStage<ProcessorContext> process(ProcessorContext context) {
        evaluateSuspectMatrixForCurrentNode();
        return null;
    }

    private void evaluateSuspectMatrixForCurrentNode() {
        List<String> nodeIds = nodeStore.getAllNodes();
        SuspectRow suspectRowForCurrentNode = suspectStore.getSuspectRowForNode(nodeStore.getCurrentNode().getId());
        for(String nodeId: nodeIds) {
            Long heartBeatTimeStamp = heartBeatStore.getHeartBeatForNode(nodeId);
            Long currentTimeStamp = System.currentTimeMillis();
            if((currentTimeStamp - heartBeatTimeStamp) > healthyThreshold * gossipInterval) {
                suspectRowForCurrentNode.updateState(nodeId, true);
            }
        }
    }
}

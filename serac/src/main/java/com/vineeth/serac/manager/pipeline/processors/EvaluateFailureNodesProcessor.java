package com.vineeth.serac.manager.pipeline.processors;

import com.vineeth.serac.manager.pipeline.IProcessor;
import com.vineeth.serac.manager.pipeline.ProcessorContext;
import com.vineeth.serac.store.nodestore.Node;
import com.vineeth.serac.store.nodestore.NodeStore;
import com.vineeth.serac.store.suspectstore.SuspectStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EvaluateFailureNodesProcessor implements IProcessor {
    private static final Logger logger = LoggerFactory.getLogger(EvaluateFailureNodesProcessor.class);

    private NodeStore nodeStore;
    private SuspectStore suspectStore;
    private float quorumPercentage;

    public EvaluateFailureNodesProcessor(NodeStore nodeStore, SuspectStore suspectStore) {
        this(nodeStore, suspectStore, 0.5f);
    }

    public EvaluateFailureNodesProcessor(NodeStore nodeStore, SuspectStore suspectStore, float quorumPercentage) {
        this.quorumPercentage = quorumPercentage;
        this.nodeStore = nodeStore;
        this.suspectStore = suspectStore;
    }

    @Override
    public CompletableFuture<ProcessorContext> process(ProcessorContext context) {
        CompletableFuture<ProcessorContext> future = new CompletableFuture<>();
        evaluateFailureNodes();
        future.complete(context);
        return future;
    }

    private void evaluateFailureNodes() {
        List<String> nodeIdsFromNodeStore = nodeStore.getAllNodeIds();
        int totalNodes = nodeIdsFromNodeStore.size();
        for(String nodeId : nodeIdsFromNodeStore) {
            Node node = nodeStore.getNodeById(nodeId);
            if(node.isHealthy()) {
                int suspectCount = suspectStore.getSuspectCountForNodeId(nodeId);
                if(suspectCount / totalNodes > quorumPercentage) {
                    logger.info("Node {} marked FAILED by Node {}", node.getId(), nodeStore.getCurrentNode().getId());
                    node.setHealthy(false);
                }
            }
        }
    }
}

package com.vineeth.serac.manager.pipeline.processors;

import com.vineeth.serac.manager.pipeline.IProcessor;
import com.vineeth.serac.manager.pipeline.ProcessorContext;
import com.vineeth.serac.store.nodestore.Node;
import com.vineeth.serac.store.nodestore.NodeStore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CreateNodeListProcessor implements IProcessor {
    private NodeStore nodeStore;

    public CreateNodeListProcessor(NodeStore nodeStore) {
        this.nodeStore = nodeStore;
    }

    @Override
    public CompletableFuture<ProcessorContext> process(ProcessorContext context) {
        CompletableFuture<ProcessorContext> future = new CompletableFuture<>();
        List<Node> nodeList = getCurrentListFromNodeStore();
        context.setNodesListToSend(nodeList);
        future.complete(context);
        return future;
    }

    private List<Node> getCurrentListFromNodeStore() {
        List<Node> nodeList = new ArrayList<>();
        List<String> nodeIds = nodeStore.getAllNodeIds();
        nodeIds.remove(nodeStore.getCurrentNode().getId());
        for(String nodeId : nodeIds) {
            Node node = nodeStore.getNodeById(nodeId);
            if(node.isHealthy()) {
                nodeList.add(node);
            }
        }
        return nodeList;
    }
}

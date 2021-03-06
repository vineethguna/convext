package com.vineeth.serac.manager.pipeline.processors;


import com.vineeth.serac.manager.pipeline.IProcessor;
import com.vineeth.serac.manager.pipeline.ProcessorContext;
import com.vineeth.serac.messages.GossipMessage;
import com.vineeth.serac.rpc.client.SeracRpcClient;
import com.vineeth.serac.store.nodestore.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SendGossipMessageRpcProcessor implements IProcessor {
    private static final Logger logger = LoggerFactory.getLogger(SendGossipMessageRpcProcessor.class);

    private ExecutorService executorService;

    public SendGossipMessageRpcProcessor() {
        executorService = Executors.newFixedThreadPool(10);
    }

    @Override
    public CompletableFuture<ProcessorContext> process(ProcessorContext context) {
        CompletableFuture<ProcessorContext> future = new CompletableFuture<>();
        sendGossipMessageToNodesViaRpc(context.getNodesListToSend(), context.getGossipMessageToSend());
        future.complete(context);
        return future;
    }

    private void sendGossipMessageToNodesViaRpc(List<Node> nodeList, GossipMessage gossipMessageToSend) {
        for(Node node : nodeList) {
            executorService.submit(() -> {
                try {
                    logger.info("Sending gossip message to Node id {} with host {} and port {}",
                            node.getId(), node.getHost(), node.getPort());
                    SeracRpcClient rpcClient = new SeracRpcClient(node.getHost(), node.getPort());
                    rpcClient.callProcessMessage(gossipMessageToSend);
                } catch (Exception e) {
                    logger.error("Error calling rpc processMessage", e);
                }
            });
        }
    }
}

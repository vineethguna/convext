package com.vineeth.serac;


import com.vineeth.serac.manager.SeracManager;
import com.vineeth.serac.server.SeracServer;
import com.vineeth.serac.server.SeracService;
import com.vineeth.serac.store.HeartBeatStore;
import com.vineeth.serac.store.MessageStore;
import com.vineeth.serac.store.nodestore.Node;
import com.vineeth.serac.store.nodestore.NodeStore;
import com.vineeth.serac.store.suspectstore.SuspectStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Serac {
    private static final Logger logger = LoggerFactory.getLogger(Serac.class);

    private SeracManager seracManager;
    private SeracService seracService;
    private SeracServer seracServer;
    private SeracConfiguration configuration;
    private ExecutorService serverExecutorService;
    private Node currentNode;

    private MessageStore messageStore;
    private NodeStore nodeStore;
    private HeartBeatStore heartBeatStore;
    private SuspectStore suspectStore;

    public Serac(SeracConfiguration configuration) throws Exception {
        this.configuration = configuration;
        initializeNode();
        initializeStores();
        seracManager = new SeracManager(messageStore, nodeStore, heartBeatStore, suspectStore, configuration);
        seracService = new SeracService(seracManager);
        seracServer = new SeracServer(seracService, configuration.getGossipServerPort());
        serverExecutorService = Executors.newSingleThreadExecutor();
    }

    private void initializeNode() throws Exception {
        InetAddress inetAddress = InetAddress.getLocalHost();

        currentNode = new Node();
        currentNode.setHost(inetAddress.getHostAddress());
        currentNode.setPort(configuration.getGossipServerPort());
        currentNode.setHealthy(true);
        currentNode.setId(configuration.getNodeId());
    }

    private void initializeStores() {
        logger.info("Initializing data stores for serac");
        messageStore = new MessageStore();
        nodeStore = new NodeStore(currentNode);
        heartBeatStore = new HeartBeatStore(nodeStore);
        suspectStore = new SuspectStore(nodeStore);
    }

    public void start() {
        serverExecutorService.submit(() -> {
            try {
                seracServer.start();
            } catch (Exception e) {
                logger.error("Starting serac server failed", e);
            }
        });
        seracManager.startPipeline();
    }

    public void connectToPeerNode() throws Exception {
        seracManager.connectToPeerNode(configuration.getPeerHost(), configuration.getPeerPort());
    }

    public void stop() {
        logger.info("Stopping serac");
        seracManager.stopPipeline();
        seracServer.stop();
    }
}

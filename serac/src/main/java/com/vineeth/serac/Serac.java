package com.vineeth.serac;


import com.vineeth.serac.manager.SeracManager;
import com.vineeth.serac.server.SeracServer;
import com.vineeth.serac.server.SeracService;
import com.vineeth.serac.store.HeartBeatStore;
import com.vineeth.serac.store.MessageStore;
import com.vineeth.serac.store.nodestore.NodeStore;
import com.vineeth.serac.store.suspectstore.SuspectStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Serac {
    private static final Logger logger = LoggerFactory.getLogger(Serac.class);

    private SeracManager seracManager;
    private SeracService seracService;
    private SeracServer seracServer;
    private ExecutorService serverExecutorService;

    private MessageStore messageStore;
    private NodeStore nodeStore;
    private HeartBeatStore heartBeatStore;
    private SuspectStore suspectStore;

    public Serac(SeracConfiguration configuration) {
        initializeStores();
        seracManager = new SeracManager(messageStore, nodeStore, heartBeatStore, suspectStore, configuration);
        seracService = new SeracService(seracManager);
        seracServer = new SeracServer(seracService, configuration.getGossipServerPort());
        serverExecutorService = Executors.newSingleThreadExecutor();
    }

    private void initializeStores() {
        logger.info("Initializing data stores for serac");
        messageStore = new MessageStore();
        nodeStore = new NodeStore(null);
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

    public void stop() {
        logger.info("Stopping serac");
        seracServer.stop();
    }
}

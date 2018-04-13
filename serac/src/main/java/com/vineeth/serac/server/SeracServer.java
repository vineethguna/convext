package com.vineeth.serac.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeracServer {
    private static final Logger logger = LoggerFactory.getLogger(SeracServer.class);

    private Server server;

    public SeracServer(SeracService seracService, int port) {
        this.server = ServerBuilder.forPort(port).addService(seracService).build();
    }

    public void start() throws Exception {
        logger.info("Starting Serac GRPC Server to listen to messages");
        server.start();
    }

    public void stop() {
        logger.info("Stoping Serac GRPC Server");
        if (server != null) {
            server.shutdown();
        }
    }
}

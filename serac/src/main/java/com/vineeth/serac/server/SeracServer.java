package com.vineeth.serac.server;

import com.vineeth.serac.gossip.Gossip;
import com.vineeth.serac.server.handlers.SeracHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

public class SeracServer {
    private Server server;
    private Gossip gossip;
    private ContextHandlerCollection handlerCollection = new ContextHandlerCollection();

    public SeracServer(Gossip gossip) {
        this(gossip,5142);
    }

    public SeracServer(Gossip gossip, int port) {
        this.gossip = gossip;

        server = new Server(port);
        ServerConnector serverConnector = new ServerConnector(server);
        server.addConnector(serverConnector);

        SeracHandler gossipHandler = new SeracHandler(gossip);
        ContextHandler healthCheckContextHandler = new ContextHandler();
        healthCheckContextHandler.setContextPath("/gossip");
        healthCheckContextHandler.setHandler(gossipHandler);
        healthCheckContextHandler.setAllowNullPathInfo(true);
        handlerCollection.addHandler(gossipHandler);

        server.setHandler(handlerCollection);
    }

    public void start() throws Exception {
        server.start();
    }

    public void stop() throws Exception {

    }
}

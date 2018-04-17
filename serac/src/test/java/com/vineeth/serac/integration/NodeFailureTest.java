package com.vineeth.serac.integration;

import com.vineeth.serac.Serac;
import com.vineeth.serac.SeracConfiguration;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

public class NodeFailureTest {
    private static final Logger logger = LoggerFactory.getLogger(NodeFailureTest.class);

    @Test
    public void nodeFailureTest() throws Exception {
        InetAddress inetAddress = InetAddress.getLocalHost();

        SeracConfiguration node1Configuration = new SeracConfiguration();
        node1Configuration.setNodeId("1");
        node1Configuration.setGossipInterval(5000L);
        node1Configuration.setHealthyThreshold(2L);
        node1Configuration.setGossipServerPort(5001);
        Serac node1 = new Serac(node1Configuration);

        SeracConfiguration node2Configuration = new SeracConfiguration();
        node2Configuration.setNodeId("2");
        node2Configuration.setGossipInterval(5000L);
        node2Configuration.setHealthyThreshold(2L);
        node2Configuration.setGossipServerPort(5002);
        node2Configuration.setPeerHost(inetAddress.getHostAddress());
        node2Configuration.setPeerPort(5001);
        Serac node2 = new Serac(node2Configuration);

        SeracConfiguration node3Configuration = new SeracConfiguration();
        node3Configuration.setNodeId("3");
        node3Configuration.setGossipInterval(5000L);
        node3Configuration.setHealthyThreshold(2L);
        node3Configuration.setGossipServerPort(5003);
        node3Configuration.setPeerHost(inetAddress.getHostAddress());
        node3Configuration.setPeerPort(5002);
        Serac node3 = new Serac(node3Configuration);

        node1.start();
        Thread.sleep(2000);
        node2.start();
        node2.connectToPeerNode();
        Thread.sleep(5000);
        node3.start();
        node3.connectToPeerNode();
        Thread.sleep(10000);
        node3.stop();
        logger.info("Stopped node3");
        Thread.sleep(30000);
        node1.stop();
        node2.stop();
    }
}

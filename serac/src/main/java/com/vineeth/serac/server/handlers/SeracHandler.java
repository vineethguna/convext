package com.vineeth.serac.server.handlers;


import com.vineeth.serac.gossip.Gossip;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SeracHandler extends AbstractHandler{

    public SeracHandler(Gossip gossip) {

    }

    public void handle(String s, Request request, HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse) throws IOException, ServletException {

    }
}

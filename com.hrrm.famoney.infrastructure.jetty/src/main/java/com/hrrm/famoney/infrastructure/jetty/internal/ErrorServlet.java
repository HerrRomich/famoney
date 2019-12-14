package com.hrrm.famoney.infrastructure.jetty.internal;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.component.propertytypes.ServiceRanking;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContextSelect;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardServletPattern;

@Component(service = Servlet.class, scope = ServiceScope.SINGLETON)
@ServiceRanking(Integer.MIN_VALUE)
@HttpWhiteboardServletPattern("/*")
@HttpWhiteboardContextSelect("("
    + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME
    + "=com.hrrm.famoney*)")
public class ErrorServlet extends GenericServlet {

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) res;
        httpResponse.setStatus(404);
        httpResponse.getWriter().append("Not found!");
        httpResponse.flushBuffer();
    }

}

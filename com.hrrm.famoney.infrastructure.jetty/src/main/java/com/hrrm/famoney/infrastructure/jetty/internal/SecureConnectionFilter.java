package com.hrrm.famoney.infrastructure.jetty.internal;

import java.io.IOException;
import java.net.URL;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceRanking;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContextSelect;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardFilterPattern;

@Component(service = Filter.class)
@HttpWhiteboardFilterPattern("/*")
@HttpWhiteboardContextSelect("(" + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME + "=com.hrrm.famoney*)")
@ServiceRanking(100)
public class SecureConnectionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Nothing to init.
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        if ("http".equals(request.getScheme())) {
            String query = request.getQueryString() != null ? "?" + request.getQueryString() : "";
            URL newUrl = new URL("https", request.getServerName(), 8443, request.getRequestURI() + query);
            response.sendRedirect(newUrl.toString());
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Nothing to destroy.
    }

}

package com.hrrm.famoney.infrastructure.jaxrs.internal;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContextSelect;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardFilterPattern;

@Component(service = Filter.class)
@HttpWhiteboardFilterPattern("/*")
@HttpWhiteboardContextSelect("("
        + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME
        + "=com.hrrm.famoney.api)")
public class ApiSpecRedirectionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Nothing to init.
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        if (StringUtils.isEmpty(httpRequest.getPathInfo()) || "/".equals(httpRequest
            .getPathInfo())) {
            httpResponse.sendRedirect(httpRequest.getContextPath()
                    + "/spec/");
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        // Notjhing to destroy
    }

}

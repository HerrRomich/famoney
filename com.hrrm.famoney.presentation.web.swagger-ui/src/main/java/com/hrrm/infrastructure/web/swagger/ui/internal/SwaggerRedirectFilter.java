package com.hrrm.infrastructure.web.swagger.ui.internal;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContextSelect;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardFilterPattern;

@Component(service = Filter.class, scope = ServiceScope.SINGLETON)
@HttpWhiteboardFilterPattern("/*")
@HttpWhiteboardContextSelect("(" +
        HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME +
        "=com.hrrm.famoney.api.ui)")
public class SwaggerRedirectFilter implements Filter {

    private static final String INDEX_HTML = "/index.html";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Nothing to initialize
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if ( StringUtils.isEmpty(httpRequest.getPathInfo())) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/");
        } else if ("/".equals(httpRequest.getPathInfo())) {
            RequestDispatcher requestDispatcherToIndexHtml = httpRequest.getRequestDispatcher(INDEX_HTML);
            requestDispatcherToIndexHtml.forward(httpRequest, httpResponse);
        } else {
            chain.doFilter(httpRequest, httpResponse);
        }

    }

    @Override
    public void destroy() {
        // Nothing to destroy
    }

}

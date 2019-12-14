package com.hrrm.famoney.infrastructure.jetty;

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

public abstract class StartPageRedirectFilter implements Filter {

    private static final String INDEX_HTML = "/index.html";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Nothing to initialize
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        if (StringUtils.isEmpty(httpRequest.getPathInfo())) {
            httpResponse.sendRedirect(httpRequest.getContextPath()
                + "/");
        } else if ("/".equals(httpRequest.getPathInfo())) {
            RequestDispatcher requestDispatcherToIndexHtml = httpRequest.getRequestDispatcher(getStartPagePath());
            requestDispatcherToIndexHtml.forward(httpRequest, httpResponse);
        } else {
            chain.doFilter(httpRequest, httpResponse);
        }

    }

    protected String getStartPagePath() {
        return INDEX_HTML;
    }

    @Override
    public void destroy() {
        // Nothing to destroy
    }

}

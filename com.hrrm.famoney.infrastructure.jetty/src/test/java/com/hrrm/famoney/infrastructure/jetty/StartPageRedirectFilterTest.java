package com.hrrm.famoney.infrastructure.jetty;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StartPageRedirectFilterTest {

    private static final String INDEX_HTML = "/index.html";
    private static final String CONTEXT_PATH = "/context-path";
    StartPageRedirectFilter startPageRedirectFilterUnderTest;

    @BeforeEach
    public void setUp() {
        startPageRedirectFilterUnderTest = new StartPageRedirectFilter() {
        };
    }

    @Test
    public void testWhenEmptyPathInfoThenRedirectToSlash() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getPathInfo()).thenReturn(null);
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        startPageRedirectFilterUnderTest.doFilter(request, response, chain);

        verify(response).sendRedirect(CONTEXT_PATH
                + "/");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void testWhenSlashPathInfoThenForwardToIndex() throws IOException, ServletException {
        RequestDispatcher requestDispathcer = mock(RequestDispatcher.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getPathInfo()).thenReturn("/");
        when(request.getRequestDispatcher(eq(INDEX_HTML))).thenReturn(requestDispathcer);
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        startPageRedirectFilterUnderTest.doFilter(request, response, chain);

        verify(chain, never()).doFilter(request, response);
        verify(requestDispathcer).forward(request, response);
    }

    @Test
    public void testWhenOthersPathInfoThenDoFilter() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getPathInfo()).thenReturn(INDEX_HTML);
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        startPageRedirectFilterUnderTest.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    public void testNoop() throws ServletException {
        FilterConfig filterConfig = mock(FilterConfig.class);
        startPageRedirectFilterUnderTest.init(filterConfig);
        startPageRedirectFilterUnderTest.destroy();
    }

}

package com.hrrm.infrastructure.web.swagger.ui;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import com.hrrm.infrastructure.web.swagger.ui.internal.SwaggerRedirectFilter;

public class SwaggerRedirectFilterTest {
    
    private static final String CONTEXT_PATH = "/context-path";
    SwaggerRedirectFilter swaggerRedirectFilterUnderTest; 

    @Before
    public void setUp() throws Exception {
        swaggerRedirectFilterUnderTest = new SwaggerRedirectFilter();
    }

    @Test
    public void whenEmptyPathInfoThenRedirectToSlash() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getPathInfo()).thenReturn(null);
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        
        swaggerRedirectFilterUnderTest.doFilter(request, response, chain);
        
        verify(response).sendRedirect(CONTEXT_PATH + "/");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void whenSlashPathInfoThenForwardToIndex() throws IOException, ServletException {
        var requestDispathcer = mock(RequestDispatcher.class);
        var request = mock(HttpServletRequest.class);
        when(request.getPathInfo()).thenReturn("/");
        when(request.getRequestDispatcher(eq("/index.html"))).thenReturn(requestDispathcer);
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        var response = mock(HttpServletResponse.class);
        var chain = mock(FilterChain.class);
        
        swaggerRedirectFilterUnderTest.doFilter(request, response, chain);
        
        verify(chain, never()).doFilter(request, response);
        verify(requestDispathcer).forward(request, response);
    }

    @Test
    public void whenOthersPathInfoThenDoFilter() throws IOException, ServletException {
        var request = mock(HttpServletRequest.class);
        when(request.getPathInfo()).thenReturn("/index.html");
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        var response = mock(HttpServletResponse.class);
        var chain = mock(FilterChain.class);
        
        swaggerRedirectFilterUnderTest.doFilter(request, response, chain);
        
        verify(chain).doFilter(request, response);
    }

}

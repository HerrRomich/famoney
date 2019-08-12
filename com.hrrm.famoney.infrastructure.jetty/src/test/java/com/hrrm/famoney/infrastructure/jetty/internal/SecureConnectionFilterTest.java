package com.hrrm.famoney.infrastructure.jetty.internal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URL;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

public class SecureConnectionFilterTest {

    private static final String SERVER_NAME = "server_name";
    private static final Integer SERVER_PORT = 8080;
    private static final String REQUEST_URI = "/part1/part2";
    private static final String QUERY_STRING = "parameter1=value1&paramter2=value2";
    private static final int SECURE_SERVER_PORT = 8443;
    private SecureConnectionFilter secureConnectionFilterUnderTest;

    @Before
    public void setUp() {
        secureConnectionFilterUnderTest = new SecureConnectionFilter();
    }

    @Test
    public void testNoop() throws ServletException {
        FilterConfig filterConfig = mock(FilterConfig.class);
        secureConnectionFilterUnderTest.init(filterConfig);
        secureConnectionFilterUnderTest.destroy();
    }

    @Test
    public void testWhenSecureProtocolThenDoFilter() throws IOException, ServletException {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getScheme()).thenReturn("https");
        HttpServletResponse res = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        secureConnectionFilterUnderTest.doFilter(req, res, chain);

        verify(chain).doFilter(req, res);
    }

    @Test
    public void testWhenNotSecureWithQueryThenRedirectToSecureWithQuery() throws IOException, ServletException {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getScheme()).thenReturn("http");
        when(req.getServerName()).thenReturn(SERVER_NAME);
        when(req.getServerPort()).thenReturn(SERVER_PORT);
        when(req.getRequestURI()).thenReturn(REQUEST_URI);
        when(req.getQueryString()).thenReturn(QUERY_STRING);
        HttpServletResponse res = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        secureConnectionFilterUnderTest.doFilter(req, res, chain);

        String file = REQUEST_URI + "?" + QUERY_STRING;
        String secureLocation = new URL("https", SERVER_NAME, SECURE_SERVER_PORT, file).toString();
        verify(res).sendRedirect(eq(secureLocation));
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    public void testWhenNotSecureWithoutQueryThenRedirectToSecureWithoutQuery() throws IOException, ServletException {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getScheme()).thenReturn("http");
        when(req.getServerName()).thenReturn(SERVER_NAME);
        when(req.getServerPort()).thenReturn(SERVER_PORT);
        when(req.getRequestURI()).thenReturn(REQUEST_URI);
        HttpServletResponse res = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        secureConnectionFilterUnderTest.doFilter(req, res, chain);

        String secureLocation = new URL("https", SERVER_NAME, SECURE_SERVER_PORT, REQUEST_URI).toString();
        verify(res).sendRedirect(eq(secureLocation));
        verify(chain, never()).doFilter(any(), any());
    }

}

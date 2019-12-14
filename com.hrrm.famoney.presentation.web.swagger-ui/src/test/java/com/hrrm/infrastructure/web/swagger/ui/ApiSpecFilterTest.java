package com.hrrm.infrastructure.web.swagger.ui;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import com.hrrm.infrastructure.web.swagger.ui.internal.ApiSpecFilter;

public class ApiSpecFilterTest {

    ApiSpecFilter       apiSpecFilterUnderTest;
    private SwaggerApis swaggerApis;

    @Before
    public void setup() {
        swaggerApis = mock(SwaggerApis.class);
        apiSpecFilterUnderTest = new ApiSpecFilter(swaggerApis);
    }

    @Test
    public void whenApisJsThenGenerate() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getPathInfo()).thenReturn("apis.js");
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        apiSpecFilterUnderTest.doFilter(request, response, chain);

        verify(swaggerApis).writeApisJs(request, response);
        verifyNoMoreInteractions(chain);
    }

    @Test
    public void whenApiSpecJsonThenGenerate() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String apiName = "accounts";
        when(request.getPathInfo()).thenReturn(apiName
            + "/api.json");
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        apiSpecFilterUnderTest.doFilter(request, response, chain);

        verify(swaggerApis).writeApiJson(apiName, request, response);
        verifyNoMoreInteractions(chain);
    }

    @Test
    public void whenOtherPathsThenDoFilter() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getPathInfo()).thenReturn("accounts/accounts");
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        apiSpecFilterUnderTest.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

}

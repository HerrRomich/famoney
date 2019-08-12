package com.hrrm.famoney.infrastructure.jetty.internal;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

public class ErrorServletTest {

    private ErrorServlet errorServletUnderTest;

    @Before
    public void setUp() {
        errorServletUnderTest = new ErrorServlet();
    }

    @Test
    public void testStaticService() throws ServletException, IOException {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);

        PrintWriter respPrintWriter = mock(PrintWriter.class);
        when(resp.getWriter()).thenReturn(respPrintWriter);
        errorServletUnderTest.service(req, resp);

        InOrder inOrder = inOrder(resp, respPrintWriter);

        inOrder.verify(resp)
            .setStatus(404);
        inOrder.verify(resp)
            .flushBuffer();
        inOrder.verifyNoMoreInteractions();
    }

}

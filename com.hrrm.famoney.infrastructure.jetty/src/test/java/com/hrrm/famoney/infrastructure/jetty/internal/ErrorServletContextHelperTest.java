package com.hrrm.famoney.infrastructure.jetty.internal;

import org.junit.Before;
import org.junit.Test;

public class ErrorServletContextHelperTest {

    private ErrorServletContextHelper errorServletContextHelperUnderTest;

    @Before
    public void setUp() {
        errorServletContextHelperUnderTest = new ErrorServletContextHelper();
    }

    @Test
    public void testServletContextHelper() {
        errorServletContextHelperUnderTest.toString();
    }

}

package com.hrrm.famoney.infrastructure.jetty.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ErrorServletContextHelperTest {

    private ErrorServletContextHelper errorServletContextHelperUnderTest;

    @BeforeEach
    public void setUp() {
        errorServletContextHelperUnderTest = new ErrorServletContextHelper();
    }

    @Test
    public void testServletContextHelper() {
        errorServletContextHelperUnderTest.toString();
    }

}

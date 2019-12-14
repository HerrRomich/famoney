package com.hrrm.infrastructure.web.swagger.ui.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.Map;
import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.service.log.Logger;
import org.powermock.reflect.Whitebox;

import com.hrrm.famoney.infrastructure.jaxrs.ApiSpecification;

public class SwaggerApisImplTest {

    private static final String TEST_API = "test_api";
    SwaggerApisImpl swaggerApisUnderTest;
    private Map<String, ApiSpecification> apiSpecifications;

    @BeforeEach
    public void setUp() throws Exception {
        swaggerApisUnderTest = new SwaggerApisImpl(mock(Logger.class));
        apiSpecifications = Whitebox.getInternalState(swaggerApisUnderTest, "apiSpecifications");
    }

    @Test
    public void testWriteApiJsonWithoutApi() {
        HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockedResponse = mock(HttpServletResponse.class);

        assertFalse(apiSpecifications.containsKey(TEST_API));
        assertThrows(NoSuchElementException.class, () -> swaggerApisUnderTest.writeApiJson(TEST_API,
            mockedRequest, mockedResponse));
    }

}

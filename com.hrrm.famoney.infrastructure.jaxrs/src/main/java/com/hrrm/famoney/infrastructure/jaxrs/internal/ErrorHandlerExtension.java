package com.hrrm.famoney.infrastructure.jaxrs.internal;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JSONRequired;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsExtension;

import com.hrrm.famoney.infrastructure.jaxrs.ApiException;
import com.hrrm.famoney.infrastructure.jaxrs.impl.ApiErrorDTOImpl;

@Component(service = { ExceptionMapper.class })
@JaxrsExtension
@JSONRequired
public class ErrorHandlerExtension implements ExceptionMapper<ApiException> {

    @Activate
    public void init() {
        this.toString();
    }

    @Override
    public Response toResponse(ApiException exception) {
        final var errorDTOBuilder = ApiErrorDTOImpl.builder()
            .code(exception.getErrorCode())
            .message(exception.getErrorMessage())
            .description(exception.getErrorDescription());
        return Response.status(exception.getResponseStatus())
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .entity(errorDTOBuilder.build())
            .build();
    }

}

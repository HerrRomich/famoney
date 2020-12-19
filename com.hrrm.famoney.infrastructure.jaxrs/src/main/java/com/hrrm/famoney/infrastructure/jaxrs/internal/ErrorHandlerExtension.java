package com.hrrm.famoney.infrastructure.jaxrs.internal;

import java.util.Optional;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JSONRequired;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsExtension;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import com.hrrm.famoney.infrastructure.jaxrs.ApiErrorDTO;
import com.hrrm.famoney.infrastructure.jaxrs.ApiException;
import com.hrrm.famoney.infrastructure.jaxrs.impl.ApiErrorDTOImpl;

@Component(service = { ExceptionMapper.class })
@JaxrsExtension
@JSONRequired
@JaxrsApplicationSelect("(osgi.jaxrs.name=com.hrrm.famoney.application.api.*)")
public class ErrorHandlerExtension implements ExceptionMapper<Exception> {

    final private Logger logger;

    @Activate
    public ErrorHandlerExtension(@Reference(service = LoggerFactory.class) final Logger logger) {
        super();
        this.logger = logger;
    }

    @Override
    public Response toResponse(Exception exception) {
        ApiErrorDTO errorDTO;
        Status responseStatus;
        if (exception instanceof ApiException) {
            final var apiException = (ApiException) exception;
            errorDTO = ApiErrorDTOImpl.builder()
                .code(apiException.getErrorCode())
                .message(apiException.getErrorMessage())
                .description(Optional.ofNullable(apiException.getErrorDescription()))
                .build();
            responseStatus = apiException.getResponseStatus();
        } else {
            errorDTO = ApiErrorDTOImpl.builder()
                .code("500")
                .message("Server internal problem.")
                .description(Optional.ofNullable(exception.getMessage()))
                .build();
            responseStatus = Status.INTERNAL_SERVER_ERROR;
        }
        logger.error("Server internal problem.", exception);
        return Response.status(responseStatus)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .entity(errorDTO)
            .build();
    }

}

package com.hrrm.famoney.api.accounts.internal;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsExtension;

import com.hrrm.famoney.infrastructure.jaxrs.OperationTimestampProvider;

@Component(service = { ContainerRequestFilter.class })
@JaxrsExtension
@JaxrsApplicationSelect("(osgi.jaxrs.name=com.hrrm.famoney.application.api.accounts)")
public class OperationTimestampRequestFilter implements ContainerRequestFilter {

    private final  OperationTimestampProvider operationTimestampProvider;

    @Activate
    public OperationTimestampRequestFilter(@Reference OperationTimestampProvider operationTimestampProvider) {
        super();
        this.operationTimestampProvider = operationTimestampProvider;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        operationTimestampProvider.setTimestamp();
    }

}

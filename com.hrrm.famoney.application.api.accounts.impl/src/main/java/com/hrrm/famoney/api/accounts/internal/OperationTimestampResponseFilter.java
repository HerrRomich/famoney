package com.hrrm.famoney.api.accounts.internal;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsExtension;

import com.hrrm.famoney.infrastructure.jaxrs.OperationTimestampProvider;

@Component(service = { ContainerResponseFilter.class })
@JaxrsExtension
@JaxrsApplicationSelect("(osgi.jaxrs.name=com.hrrm.famoney.application.api.accounts)")
public class OperationTimestampResponseFilter implements ContainerResponseFilter {

    private final OperationTimestampProvider operationTimestampProvider;

    @Activate
    public OperationTimestampResponseFilter(
            @Reference OperationTimestampProvider operationTimestampProvider) {
        super();
        this.operationTimestampProvider = operationTimestampProvider;
    }

    @Override
    public void filter(ContainerRequestContext requestContext,
        ContainerResponseContext responseContext) throws IOException {
        responseContext.getHeaders()
            .add("fm-operation-timestamp", operationTimestampProvider.getTimestamp()
                .toString());
    }

}

package com.hrrm.infrastructure.web.swagger.ui.internal;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContext;

@Component(service = ServletContextHelper.class)
@HttpWhiteboardContext(name = "com.hrrm.famoney.api.spec", path = "/api/spec")
public class SwaggerServletContextHelper extends ServletContextHelper {

    @Activate
    public SwaggerServletContextHelper(BundleContext bundleContext) {
        super(bundleContext.getBundle());
    }

}

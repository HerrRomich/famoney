package com.hrrm.infrastructure.web.swagger.ui.internal;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContext;

@Component(service = ServletContextHelper.class, scope = ServiceScope.BUNDLE)
@HttpWhiteboardContext(name = "com.hrrm.famoney.api.ui", path = "/api/ui")
public class SwaggerServletContextHelper extends ServletContextHelper {

    @Activate
    public SwaggerServletContextHelper(BundleContext bundleContext) {
        super(bundleContext.getBundle());
    }

}

package com.hrrm.famoney.infrastructure.jaxrs.internal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContext;

@Component(service = ServletContextHelper.class, scope = ServiceScope.BUNDLE)
@HttpWhiteboardContext(name = "com.hrrm.famoney.api", path = "/api")
public class ApiServletContextHelper extends ServletContextHelper {

}

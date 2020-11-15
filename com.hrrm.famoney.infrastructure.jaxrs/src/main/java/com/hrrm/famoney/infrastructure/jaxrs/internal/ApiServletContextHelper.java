package com.hrrm.famoney.infrastructure.jaxrs.internal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContext;

@Component(service = ServletContextHelper.class)
@HttpWhiteboardContext(name = "com.hrrm.famoney.apis", path = "/apis")
public class ApiServletContextHelper extends ServletContextHelper {

}

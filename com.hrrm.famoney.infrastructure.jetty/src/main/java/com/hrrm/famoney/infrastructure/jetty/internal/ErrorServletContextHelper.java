package com.hrrm.famoney.infrastructure.jetty.internal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceRanking;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContext;

@Component(service = ServletContextHelper.class)
@ServiceRanking(Integer.MIN_VALUE)
@HttpWhiteboardContext(name = "com.hrrm.famoney", path = "/")
public class ErrorServletContextHelper extends ServletContextHelper {

}

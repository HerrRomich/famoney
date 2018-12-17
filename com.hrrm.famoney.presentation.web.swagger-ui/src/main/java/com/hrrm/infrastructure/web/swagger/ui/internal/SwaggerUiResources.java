package com.hrrm.infrastructure.web.swagger.ui.internal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContextSelect;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardResource;

import com.hrrm.infrastructure.web.swagger.ui.WebResource;

@Component(scope = ServiceScope.PROTOTYPE, service = WebResource.class)
@HttpWhiteboardContextSelect("(" +
        HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME +
        "=com.hrrm.famoney.api.ui)")
@HttpWhiteboardResource(pattern = { "/*" }, prefix = "/swagger-ui")
public class SwaggerUiResources implements WebResource {

}

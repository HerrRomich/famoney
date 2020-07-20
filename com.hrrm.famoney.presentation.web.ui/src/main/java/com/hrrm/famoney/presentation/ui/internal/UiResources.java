package com.hrrm.famoney.presentation.ui.internal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContextSelect;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardResource;

import com.hrrm.famoney.infrastructure.jetty.WebResource;

@Component(service = WebResource.class)
@HttpWhiteboardContextSelect("(" + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME + "=com.hrrm.famoney.ui)")
@HttpWhiteboardResource(pattern = {
        "/*"
}, prefix = "/ui")
public class UiResources implements WebResource {

}

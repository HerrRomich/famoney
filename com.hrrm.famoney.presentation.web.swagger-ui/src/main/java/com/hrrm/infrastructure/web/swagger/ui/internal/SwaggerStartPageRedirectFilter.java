package com.hrrm.infrastructure.web.swagger.ui.internal;

import javax.servlet.Filter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContextSelect;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardFilterPattern;

import com.hrrm.famoney.infrastructure.jetty.StartPageRedirectFilter;

@Component(service = Filter.class)
@HttpWhiteboardFilterPattern("/*")
@HttpWhiteboardContextSelect("(" + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME + "=com.hrrm.famoney.api.spec)")
public class SwaggerStartPageRedirectFilter extends StartPageRedirectFilter {
}

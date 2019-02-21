package com.hrrm.infrastructure.web.swagger.ui.internal;

import java.io.IOException;
import java.net.URI;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContextSelect;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardFilterPattern;

import com.hrrm.infrastructure.web.swagger.ui.SwaggerApis;

@Component(service = Filter.class)
@HttpWhiteboardFilterPattern("/*")
@HttpWhiteboardContextSelect("(" + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME + "=com.hrrm.famoney.api.spec)")
public class ApiSpecFilter implements Filter {

    private SwaggerApis swaggerApis;

    @Activate
    public ApiSpecFilter(@Reference SwaggerApis swaggerApis) {
        super();
        this.swaggerApis = swaggerApis;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Nothing to initialize
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String apiName = null;
        if ("/apis.js".equals(httpRequest.getPathInfo())) {
            swaggerApis.writeApisJs(httpRequest, httpResponse);
        } else if ((apiName = getapiName(httpRequest.getPathInfo())) != null) {
            swaggerApis.writeApiJson(apiName, httpRequest, httpResponse);
        } else {
            chain.doFilter(httpRequest, httpResponse);
        }
    }

    private String getapiName(String pathInfo) {
        try {
            String[] pathParts = URI.create(pathInfo)
                .getPath()
                .split("/");
            if ((pathParts.length == 3) && StringUtils.isEmpty(pathParts[0]) && "api.json".equals(pathParts[2])) {
                return pathParts[1];
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void destroy() {
        // Nothing to destroy
    }

}

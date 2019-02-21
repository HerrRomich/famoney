package com.hrrm.infrastructure.web.swagger.ui.internal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.HttpHeaders;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ServiceScope;

import com.hrrm.famoney.infrastructure.jaxrs.ApiSpecification;
import com.hrrm.infrastructure.web.swagger.ui.SwaggerApis;

@Component(service = SwaggerApis.class, scope = ServiceScope.SINGLETON)
public class SwaggerApisImpl implements SwaggerApis {

    private static final String API_JSON = "api.json";
    private static final String API_JSON_PATH = API_JSON;
    private static final String API_SPEC_URL_TEMPLATE = "%1$s/%2$s/" + API_JSON_PATH;
    private static final String APIS_JS_ELEMENT_TEMPLATE = "    {url: '" + API_SPEC_URL_TEMPLATE + "', name: '%3$s'}";

    private Map<String, ApiSpecification> apiSpecifications = new ConcurrentHashMap<>();

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, bind = "bindApiSpecification", policy = ReferencePolicy.DYNAMIC)
    public void bindApiSpecification(ApiSpecification apiSpecification) {
        apiSpecifications.putIfAbsent(apiSpecification.getPath(), apiSpecification);
    }

    public void unbindApiSpecification(ApiSpecification apiSpecification) {
        apiSpecifications.remove(apiSpecification.getPath());
    }

    @Override
    public void writeApisJs(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.addHeader(HttpHeaders.CONTENT_TYPE, request.getServletContext()
            .getMimeType("apis.js"));
        try (OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream())) {
            writer.write("var apis = [\r\n");
            String separator = "";
            for (ApiSpecification apiSpecification : apiSpecifications.values()) {
                writer.write(separator);
                writer.write(String.format(APIS_JS_ELEMENT_TEMPLATE, request.getContextPath(), apiSpecification
                    .getPath(), apiSpecification.getDescription()));
                separator = ",\r\n";
            }

            writer.write("\r\n  ]\r\n");
        }
    }

    @Override
    public void writeApiJson(String apiName, HttpServletRequest request, HttpServletResponse response) {
        apiSpecifications.compute(apiName, (name, specification) -> {
            if (specification == null) {
                throw new NotFoundException();
            } else {
                sendApiJson(specification, request, response);
                return specification;
            }
        });
    }

    private void sendApiJson(ApiSpecification apiSpecification, HttpServletRequest request,
            HttpServletResponse response) {
        try {
            response.addHeader(HttpHeaders.CONTENT_TYPE, request.getServletContext()
                .getMimeType(API_JSON));

            try (InputStream openStream = apiSpecification.getSpecificationStream();
                    BufferedReader specReader = new BufferedReader(new InputStreamReader(openStream));
                    BufferedWriter specWriter = new BufferedWriter(new OutputStreamWriter(response
                        .getOutputStream()))) {
                String line;
                while ((line = specReader.readLine()) != null) {
                    specWriter.write(line + "\r\n");
                }
            }
        } catch (IOException e) {
            throw new InternalServerErrorException("Unable to stream api.json", e);
        }
    }

}

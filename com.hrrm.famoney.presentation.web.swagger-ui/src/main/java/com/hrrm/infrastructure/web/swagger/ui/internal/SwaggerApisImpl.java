package com.hrrm.infrastructure.web.swagger.ui.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.hrrm.famoney.infrastructure.jaxrs.ApiSpecification;
import com.hrrm.infrastructure.web.swagger.ui.SwaggerApis;

@Component(service = SwaggerApis.class, scope = ServiceScope.SINGLETON)
public class SwaggerApisImpl implements SwaggerApis {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String API_JSON = "api.json";
    private static final String API_SPEC_URL_TEMPLATE = "%1$s/%2$s.json";
    private static final String APIS_JS_ELEMENT_TEMPLATE = "    {url: '" + API_SPEC_URL_TEMPLATE + "', name: '%3$s'}";

    private Map<String, ApiSpecification> apiSpecifications = new ConcurrentHashMap<>();
    private final Logger logger;

    @Activate
    public SwaggerApisImpl(@Reference(service = LoggerFactory.class) final Logger logger) {
        this.logger = logger;
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, bind = "bindApiSpecification",
            policy = ReferencePolicy.DYNAMIC)
    public void bindApiSpecification(ApiSpecification apiSpecification) {
        apiSpecifications.computeIfAbsent(apiSpecification.getPath(),
                path -> {
                    logger.info("Registered new OpenAPI specification for path: {}",
                            apiSpecification.getPath());
                    return apiSpecification;
                });
    }

    public void unbindApiSpecification(ApiSpecification apiSpecification) {
        apiSpecifications.computeIfPresent(apiSpecification.getPath(),
                (path, spec) -> {
                    logger.info("Unregistered OpenAPI specification for name: {0}",
                            apiSpecification.getPath());
                    return null;
                });
    }

    @Override
    public void writeApisJs(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.debug("Writing a list of registerd OpenAPI specifications in form of JS-array into HTTP response.");
        response.addHeader(CONTENT_TYPE,
                request.getServletContext()
                    .getMimeType("apis.js"));
        try (var writer = new OutputStreamWriter(response.getOutputStream())) {
            writer.write("var apis = [\r\n");
            var separator = "";
            Collection<ApiSpecification> specPaths = apiSpecifications.values();
            for (ApiSpecification apiSpecification : specPaths) {
                writer.write(separator);
                writer.write(String.format(APIS_JS_ELEMENT_TEMPLATE,
                        request.getContextPath(),
                        apiSpecification.getPath(),
                        apiSpecification.getDescription()));
                separator = ",\r\n";
            }
            writer.write("\r\n  ]\r\n");
            logger.trace("Following list of OpenAPI specifications is written as JS-array: {}",
                    specPaths);
        }
    }

    @Override
    public void writeApiJson(String apiName, HttpServletRequest request, HttpServletResponse response) {
        logger.debug("Writing an OpenAPI specification with name: {}.",
                apiName);
        apiSpecifications.compute(apiName,
                (name, specification) -> {
                    if (specification == null) {
                        throw new NoSuchElementException("");
                    } else {
                        sendApiJson(specification,
                                request,
                                response);
                        return specification;
                    }
                });
    }

    private void sendApiJson(ApiSpecification apiSpecification, HttpServletRequest request,
            HttpServletResponse response) {
        logger.debug("Sending OpenAPI spec for ");
        response.addHeader(CONTENT_TYPE,
                request.getServletContext()
                    .getMimeType(API_JSON));
        JsonFactory jFactory = new JsonFactory();
        try (InputStream specStream = Optional.ofNullable(apiSpecification.getSpecificationStream())
            .orElseThrow();
                JsonParser jParser = jFactory.createParser(specStream);
                JsonGenerator jGenerator = jFactory.createGenerator(response.getOutputStream());) {
            String url = request.getContextPath()
                .replace("spec",
                        apiSpecification.getPath());
            pipeJson(jParser,
                    jGenerator,
                    url);
        } catch (IOException | NoSuchElementException ex) {
            response.setStatus(500);
        }
    }

    private void pipeJson(JsonParser jParser, JsonGenerator jGenerator, String url) throws IOException {
        jParser.nextToken();
        jGenerator.writeStartObject();
        while (jParser.nextToken() != JsonToken.END_OBJECT) {
            if (jParser.getCurrentToken() == JsonToken.START_OBJECT) {
                processJsonObject(jParser,
                        jGenerator);
                if ("info".equals(jParser.getCurrentName())) {
                    jGenerator.writeFieldName("servers");
                    jGenerator.writeStartArray();
                    jGenerator.writeStartObject();
                    jGenerator.writeStringField("url",
                            url);
                    jGenerator.writeEndObject();
                    jGenerator.writeEndArray();
                }
            } else if (jParser.getCurrentToken() == JsonToken.START_ARRAY) {
                processJsonArray(jParser,
                        jGenerator);
            } else {
                processJsonValue(jParser,
                        jGenerator);
            }
        }
        jGenerator.writeEndObject();
    }

    private void processJsonObject(JsonParser jParser, JsonGenerator jGenerator) throws IOException {
        jGenerator.writeStartObject();
        while (jParser.nextToken() != JsonToken.END_OBJECT) {
            processJsonValue(jParser,
                    jGenerator);
        }
        jGenerator.writeEndObject();
    }

    private void processJsonArray(JsonParser jParser, JsonGenerator jGenerator) throws IOException {
        jGenerator.writeStartArray();
        while (jParser.nextToken() != JsonToken.END_ARRAY) {
            processJsonValue(jParser,
                    jGenerator);
        }
        jGenerator.writeEndArray();
    }

    private void processJsonValue(JsonParser jParser, JsonGenerator jGenerator) throws IOException {
        if (jParser.getCurrentToken() == JsonToken.FIELD_NAME) {
            jGenerator.writeFieldName(jParser.getCurrentName());
        } else if (jParser.getCurrentToken() == JsonToken.START_ARRAY) {
            processJsonArray(jParser,
                    jGenerator);
        } else if (jParser.getCurrentToken() == JsonToken.START_OBJECT) {
            processJsonObject(jParser,
                    jGenerator);
        } else if (jParser.getCurrentToken() == JsonToken.VALUE_FALSE) {
            jGenerator.writeBoolean(false);
        } else if (jParser.getCurrentToken() == JsonToken.VALUE_TRUE) {
            jGenerator.writeBoolean(true);
        } else if (jParser.getCurrentToken() == JsonToken.VALUE_NULL) {
            jGenerator.writeNull();
        } else if (jParser.getCurrentToken() == JsonToken.VALUE_NUMBER_FLOAT) {
            jGenerator.writeNumber(jParser.getFloatValue());
        } else if (jParser.getCurrentToken() == JsonToken.VALUE_NUMBER_INT) {
            jGenerator.writeNumber(jParser.getIntValue());
        } else if (jParser.getCurrentToken() == JsonToken.VALUE_STRING) {
            jGenerator.writeString(jParser.getText());
        }
    }

}

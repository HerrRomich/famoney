package com.hrrm.famoney.infrastructure.jaxrs.internal;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.osgi.annotation.bundle.Capability;
import org.osgi.framework.Bundle;
import org.osgi.framework.PrototypeServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.namespace.service.ServiceNamespace;
import org.osgi.service.jaxrs.whiteboard.annotations.RequireJaxrsWhiteboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.cfg.Annotations;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

@Capability(
        attribute = {
                "objectClass:List<String>='javax.ws.rs.ext.MessageBodyReader,javax.ws.rs.ext.MessageBodyWriter'",
                "osgi.jaxrs.media.type=application/json", "osgi.jaxrs.name=jackson" },
        namespace = ServiceNamespace.SERVICE_NAMESPACE)
@RequireJaxrsWhiteboard
public class JsonProviderPrototypeServiceFactory implements
        PrototypeServiceFactory<JacksonJsonProvider> {

    JsonProviderPrototypeServiceFactory(Dictionary<String, ?> properties) {
        _properties = properties;
    }

    @Override
    public JacksonJsonProvider getService(Bundle bundle,
        ServiceRegistration<JacksonJsonProvider> registration) {

        return createJsonProvider(_properties);
    }

    @Override
    public void ungetService(Bundle bundle, ServiceRegistration<JacksonJsonProvider> registration,
        JacksonJsonProvider service) {

    }

    private Dictionary<String, ?> _properties;

    private JacksonJsonProvider createJsonProvider(Dictionary<String, ?> properties) {

        List<Annotations> list = new ArrayList<>();

        if (getBooleanProperty(properties, "jackson.annotations.enabled", true)) {
            list.add(Annotations.JACKSON);
        }

        if (getBooleanProperty(properties, "jaxb.annotations.enabled", true)) {
            list.add(Annotations.JAXB);
        }

        var mapper = new ObjectMapper();

        Jdk8Module jdk8Module = new Jdk8Module();
        jdk8Module.configureAbsentsAsNulls(true);
        mapper.registerModule(jdk8Module);
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new JacksonJsonProvider(mapper, list.toArray(new Annotations[list.size()]));
    }

    private boolean getBooleanProperty(Dictionary<String, ?> properties, String key,
        boolean defaultValue) {
        Object object = properties.get(key);
        if (object == null) {
            return defaultValue;
        } else {
            return Boolean.parseBoolean(String.valueOf(object));
        }
    }
}
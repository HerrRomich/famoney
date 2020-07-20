package com.hrrm.famoney.infrastructure.jaxrs.internal;

import static org.apache.aries.component.dsl.OSGi.all;
import static org.apache.aries.component.dsl.OSGi.coalesce;
import static org.apache.aries.component.dsl.OSGi.configuration;
import static org.apache.aries.component.dsl.OSGi.configurations;
import static org.apache.aries.component.dsl.OSGi.just;
import static org.apache.aries.component.dsl.OSGi.register;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import org.apache.aries.component.dsl.OSGi;
import org.apache.aries.component.dsl.OSGiResult;
import org.osgi.annotation.bundle.Header;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;

@Header(name = Constants.BUNDLE_ACTIVATOR, value = "${@class}")
public class Activator implements BundleActivator {

    public static final String CONFIG_PID = "org.apache.aries.jax.rs.jackson";

    public static final OSGi<Dictionary<String, ?>> CONFIGURATION = all(configurations(CONFIG_PID),
            coalesce(configuration(CONFIG_PID),
                    just(Hashtable::new))).filter(c -> !Objects.equals(c.get("enabled"),
                            "false"));

    @Override
    public void start(BundleContext context) throws Exception {
        result = CONFIGURATION.flatMap(properties -> register(new String[] {
                MessageBodyReader.class.getName(),
                MessageBodyWriter.class.getName()
        },
                new JsonProviderPrototypeServiceFactory(properties),
                getRegistrationProperties(properties)))
            .run(context);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        result.close();
    }

    private OSGiResult result;

    private Map<String, ?> getRegistrationProperties(Dictionary<String, ?> properties) {
        Map<String, Object> serviceProps = new HashMap<>();
        serviceProps.put(JaxrsWhiteboardConstants.JAX_RS_EXTENSION,
                true);
        serviceProps.put(JaxrsWhiteboardConstants.JAX_RS_MEDIA_TYPE,
                MediaType.APPLICATION_JSON);
        serviceProps.putIfAbsent(JaxrsWhiteboardConstants.JAX_RS_NAME,
                "jaxb-json");
        serviceProps.put(JaxrsWhiteboardConstants.JAX_RS_APPLICATION_SELECT,
                String.format("(%s=%s)",
                        JaxrsWhiteboardConstants.JAX_RS_NAME,
                        "com.hrrm.famoney.application.api.*"));
        serviceProps.put(Constants.SERVICE_RANKING,
                Integer.MIN_VALUE);
        serviceProps.put("jackson.jaxb.version",
                new com.fasterxml.jackson.module.jaxb.PackageVersion().version()
                    .toString());
        serviceProps.put("jackson.jaxrs.json.version",
                new com.fasterxml.jackson.jaxrs.json.PackageVersion().version()
                    .toString());

        Enumeration<String> keys = properties.keys();

        while (keys.hasMoreElements()) {
            String key = keys.nextElement();

            if (!key.startsWith(".")) {
                serviceProps.put(key,
                        properties.get(key));
            }
        }

        return serviceProps;
    }

}

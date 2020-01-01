package com.hrrm.famoney.infrastructure.persistence.internal;

import java.util.HashMap;
import java.util.Map;

import org.ops4j.pax.jdbc.config.ConfigLoader;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

@Component(name = "com.hrrm.famoney.infrastructure.persistence.DatasourceConfigLoader")
public class DatasourceConfigLoader implements ConfigLoader {

    private static final String CONFIG_LOADER_NAME = "configLoaderName";
    private Map<String, String> properties;
    private String name;

    @Activate
    protected void init(Map<String, String> properties) {
        this.properties = new HashMap<>(properties);
        name = this.properties.getOrDefault(CONFIG_LOADER_NAME, properties.get("service.pid"));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String resolve(String key) {
        return properties.get(key);
    }

}

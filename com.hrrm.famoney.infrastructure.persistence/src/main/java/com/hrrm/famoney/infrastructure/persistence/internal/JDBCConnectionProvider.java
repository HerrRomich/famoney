package com.hrrm.famoney.infrastructure.persistence.internal;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.sql.XADataSource;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.transaction.control.jdbc.JDBCConnectionProviderFactory;

@Component(name = "com.hrrm.famoney.infrastructure.persistence.JDBCConnectionProvider")
public class JDBCConnectionProvider {

    private final ServiceRegistration<org.osgi.service.transaction.control.jdbc.JDBCConnectionProvider> jdbcConnectionProviderRegistration;
    private final JDBCConnectionProviderFactory jdbcProviderFactory;
    
    @Activate
    public JDBCConnectionProvider(final BundleContext context, final Map<String, Object> properties, 
            @Reference final JDBCConnectionProviderFactory jdbcProviderFactory,
            @Reference(name = "data_source") final XADataSource ds) {
        super();
        this.jdbcProviderFactory = jdbcProviderFactory;
        final Map<String, Object> resourceProviderProperties = new HashMap<>();
        final var provider = jdbcProviderFactory.getProviderFor(ds, resourceProviderProperties);
        final Dictionary<String, String> props = new Hashtable<>();
        final var name = properties.get("name")
            .toString();
        if (name != null) {
            props.put("name", name);
        }
        jdbcConnectionProviderRegistration = context.registerService(org.osgi.service.transaction.control.jdbc.JDBCConnectionProvider.class, provider, props);
    }
    
    @Deactivate
    public void deactivate(final BundleContext context) {
        final var jdbcConnectionProvider = context.getService(jdbcConnectionProviderRegistration.getReference());
        jdbcProviderFactory.releaseProvider(jdbcConnectionProvider);
        jdbcConnectionProviderRegistration.unregister();
    }

    
    
}

package com.hrrm.famoney.infrastructure.persistence.internal;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jpa.EntityManagerFactoryBuilder;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProviderFactory;

@Component(name = "com.hrrm.famoney.infrastructure.persistence.EntityMnagerProvider")
public class EntityMnagerProvider {

    private final JPAEntityManagerProviderFactory jpaPproviderFactory;
    private final ServiceRegistration<JPAEntityManagerProvider> entityManagerProviderRegistration;

    @Activate
    public EntityMnagerProvider(final BundleContext context, final Map<String, Object> properties,
            @Reference final JPAEntityManagerProviderFactory jpaPproviderFactory, @Reference(
                    name = "data_source") final DataSource ds, @Reference(
                            name = "emfb") final EntityManagerFactoryBuilder emfb,
            @Reference final TransactionControl txControl) {
        this.jpaPproviderFactory = jpaPproviderFactory;
        final Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put("javax.persistence.dataSource", ds);
        final Map<String, Object> resourceProviderProperties = new HashMap<>();
        resourceProviderProperties.put("osgi.connection.pooling.enabled", false);
        final var entityManagerProvider = jpaPproviderFactory.getProviderFor(emfb, jpaProperties,
                resourceProviderProperties);
        final Dictionary<String, String> props = new Hashtable<>();
        final var name = properties.get("name")
            .toString();
        if (name != null) {
            props.put("name", name);
        }
        entityManagerProviderRegistration = context.registerService(JPAEntityManagerProvider.class,
                entityManagerProvider, props);
    }

    @Deactivate
    public void deactivate(final BundleContext context) {
        final var entityManagerProvider = context.getService(entityManagerProviderRegistration.getReference());
        jpaPproviderFactory.releaseProvider(entityManagerProvider);
        entityManagerProviderRegistration.unregister();
    }

}

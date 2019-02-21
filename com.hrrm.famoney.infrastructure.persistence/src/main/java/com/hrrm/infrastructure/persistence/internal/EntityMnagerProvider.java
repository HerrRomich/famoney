package com.hrrm.infrastructure.persistence.internal;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.aries.transaction.AriesTransactionManager;
import org.ops4j.pax.jdbc.hook.PreHook;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.jpa.EntityManagerFactoryBuilder;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProviderFactory;

@Component(name = "com.hrrm.famoney.entity-manager-provider")
public class EntityMnagerProvider {

    @Reference
    private JPAEntityManagerProviderFactory jpaPproviderFactory;

    @Reference(target = "(osgi.jdbc.driver.name=mariadb)")
    private DataSourceFactory dsf;

    @Reference(name = "data_source")
    private DataSource ds;

    @Reference(name = "emfb")
    private EntityManagerFactoryBuilder emfb;

    @Reference(name = "migration_hook", cardinality = ReferenceCardinality.OPTIONAL)
    private PreHook migrationHook;

    @Reference
    private AriesTransactionManager tm;

    @Reference
    private TransactionControl txControl;

    private ServiceRegistration<JPAEntityManagerProvider> entityManagerProviderRegistration;

    @Activate
    public void activate(BundleContext context, Map<String, Object> properties) throws Exception {
        if (migrationHook != null) {
            migrationHook.prepare(ds);
        }

        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put("javax.persistence.nonJtaDataSource", ds);
        Map<String, Object> resourceProviderProperties = new HashMap<>();
        resourceProviderProperties.put("osgi.connection.pooling.enabled", false);
        JPAEntityManagerProvider entityManagerProvider = jpaPproviderFactory.getProviderFor(emfb, jpaProperties, resourceProviderProperties);
        Dictionary<String, String> props = new Hashtable<>();
        String name = properties.get("name")
            .toString();
        if (name != null) {
            props.put("name", name);
        }
        entityManagerProviderRegistration = context.registerService(JPAEntityManagerProvider.class,
                                                                    entityManagerProvider, props);
    }

    @Deactivate
    public void deactivate(BundleContext context) {
        JPAEntityManagerProvider entityManagerProvider = context.getService(entityManagerProviderRegistration
            .getReference());
        jpaPproviderFactory.releaseProvider(entityManagerProvider);
        entityManagerProviderRegistration.unregister();
    }

}

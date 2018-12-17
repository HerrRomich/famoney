package com.hrrm.infrastructure.persistence.internal;

import java.util.*;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.apache.aries.transaction.AriesTransactionManager;
import org.apache.aries.transaction.jdbc.RecoverableDataSource;
import org.ops4j.pax.jdbc.hook.PreHook;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.component.annotations.*;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.jpa.EntityManagerFactoryBuilder;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProviderFactory;

@Component(name = "com.hrrm.budget.entity-manager-provider")
public class EntityMnagerProvider implements ManagedService {

    private static final String POOL_PREFIX = "pool.";
    private static final List<String> DATA_SOURCE_PROPERTIES = Arrays.asList(DataSourceFactory.JDBC_DATABASE_NAME,
	    DataSourceFactory.JDBC_DATASOURCE_NAME, DataSourceFactory.JDBC_DESCRIPTION,
	    DataSourceFactory.JDBC_INITIAL_POOL_SIZE, DataSourceFactory.JDBC_MAX_IDLE_TIME,
	    DataSourceFactory.JDBC_MAX_POOL_SIZE, DataSourceFactory.JDBC_MAX_STATEMENTS,
	    DataSourceFactory.JDBC_NETWORK_PROTOCOL, DataSourceFactory.JDBC_PASSWORD,
	    DataSourceFactory.JDBC_PORT_NUMBER, DataSourceFactory.JDBC_PROPERTY_CYCLE, DataSourceFactory.JDBC_ROLE_NAME,
	    DataSourceFactory.JDBC_SERVER_NAME, DataSourceFactory.JDBC_URL, DataSourceFactory.JDBC_USER);

    @Reference
    private JPAEntityManagerProviderFactory jpaPproviderFactory;

    @Reference(target = "(osgi.jdbc.driver.name=mariadb)")
    private DataSourceFactory dsf;

    @Reference(name = "emfb")
    private EntityManagerFactoryBuilder emfb;

    @Reference(name = "migration_hook", cardinality = ReferenceCardinality.OPTIONAL)
    private PreHook migrationHook;

    @Reference
    private AriesTransactionManager tm;

    @Reference
    private TransactionControl txControl;

    private ServiceRegistration<EntityManager> entityManagerRegistration;

    @Activate
    public void activate(BundleContext context, Map<String, Object> properties) throws Exception {
	DataSource ds = create(dsf, properties);

	if (migrationHook != null) {
	    migrationHook.prepare(ds);
	}

	Map<String, Object> jpaProperties = new HashMap<>();
	jpaProperties.put("javax.persistence.nonJtaDataSource", ds);
	Map<String, Object> resourceProviderProperties = new HashMap<>();
	resourceProviderProperties.put("osgi.connection.pooling.enabled", false);
	EntityManager entityManager = jpaPproviderFactory
	    .getProviderFor(emfb, jpaProperties, resourceProviderProperties)
	    .getResource(txControl);

	Dictionary<String, String> props = new Hashtable<>();
	String name = properties.get("name").toString();
	if (name != null) {
	    props.put("name", name);
	}
	entityManagerRegistration = context.registerService(EntityManager.class, entityManager, props);
    }
    
    private DataSource create(DataSourceFactory dsf2, Map<String, Object> properties) throws Exception {
	Properties config = DATA_SOURCE_PROPERTIES.stream()
	    .filter(properties::containsKey)
	    .collect(Properties::new, (props, key) -> props.put(key, properties.get(key)), Properties::putAll);
	XADataSource ds = dsf.createXADataSource(getNonPoolProps(config));
	RecoverableDataSource mds = new RecoverableDataSource();
	mds.setUsername(config.getProperty(DataSourceFactory.JDBC_USER));
	mds.setPassword(config.getProperty(DataSourceFactory.JDBC_PASSWORD));
	mds.setDataSource(ds);
	mds.setTransactionManager(tm);
	String name = properties.get("name").toString();
	if (name != null) {
	    mds.setName("accounts");
	}
	// BeanConfig.configure(mds, getPoolProps(props));
	mds.start();
	return mds;
    }

    protected Map<String, String> getPoolProps(Properties props) {
	Map<String, String> poolProps = new HashMap<String, String>();
	for (Object keyO : props.keySet()) {
	    String key = (String) keyO;
	    if (key.startsWith(POOL_PREFIX)) {
		String strippedKey = key.substring(POOL_PREFIX.length());
		poolProps.put(strippedKey, (String) props.get(key));
	    }
	}
	return poolProps;
    }

    protected Properties getNonPoolProps(Properties props) {
	Properties dsProps = new Properties();
	for (Object keyO : props.keySet()) {
	    String key = (String) keyO;
	    if (!key.startsWith(POOL_PREFIX)) {
		dsProps.put(key, props.get(key));
	    }
	}
	return dsProps;
    }

    @Override
    public void updated(Dictionary<String, ?> properties) throws ConfigurationException {
	properties.size();
    }

}

package com.hrrm.famoney.domain.datadirectory.migration;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.ops4j.pax.jdbc.hook.PreHook;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.hrrm.famoney.domain.datadirectory.DataDictionaryDomainEntity;

@Component(service = PreHook.class, property = { "name=famoney.data-directory" })
public class DataDirectoryMigration implements PreHook {

    private BundleContext context;

    @Activate
    public void activate(BundleContext context) {
        this.context = context;
    }

    @Override
    public void prepare(DataSource ds) throws SQLException {
        final Bundle bundle = context.getBundle();
        final ClassLoader classLoader = bundle.adapt(BundleWiring.class)
            .getClassLoader();
        final String locations = "classpath:" +
                this.getClass()
                    .getPackage()
                    .getName()
                    .replace('.', '/');
        final String[] locationArray = locations.split(",");
        final Flyway flyway = Flyway.configure(classLoader)
            .dataSource(ds)
            .locations(locationArray)
            .schemas(DataDictionaryDomainEntity.SCHEMA_NAME)
            .load();
        flyway.migrate();
    }

}

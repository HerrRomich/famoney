package com.hrrm.famoney.domain.masterdata.migrations;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.ops4j.pax.jdbc.hook.PreHook;
import org.osgi.annotation.bundle.Capability;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LoggerFactory;

import com.hrrm.famoney.domain.masterdata.migrations.v1.V1M2EntryCategories;
import com.hrrm.famoney.infrastructure.persistence.migrations.MigrationNamespace;

@Component(service = PreHook.class, property = {
        "name=famoney.master-data"
})
@Capability(namespace = MigrationNamespace.MIGRATION_NAMESPACE, attribute = {
        "migration.domain=famoney.master-data"
}, version = "1.0")
public class MasterDataMigration implements PreHook {

    private final BundleContext context;
    private final LoggerFactory loggerFactory;

    @Activate
    public MasterDataMigration(final BundleContext context, @Reference final LoggerFactory loggerFactory) {
        this.context = context;
        this.loggerFactory = loggerFactory;
    }

    @Override
    public void prepare(final DataSource ds) throws SQLException {
        final Bundle bundle = context.getBundle();
        final ClassLoader classLoader = bundle.adapt(BundleWiring.class)
            .getClassLoader();
        final String locations = "classpath:" +
            this.getClass()
                .getPackage()
                .getName()
                .replace('.',
                        '/');
        final String[] locationArray = locations.split(",");
        final Flyway flyway = Flyway.configure(classLoader)
            .dataSource(ds)
            .schemas("master_data")
            .defaultSchema("master_data")
            .locations(locationArray)
            .javaMigrations(new V1M2EntryCategories(loggerFactory))
            .load();
        flyway.migrate();
    }

}

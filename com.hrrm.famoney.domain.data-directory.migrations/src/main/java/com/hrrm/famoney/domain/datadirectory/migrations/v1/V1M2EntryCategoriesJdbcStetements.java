package com.hrrm.famoney.domain.datadirectory.migrations.v1;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.hrrm.famoney.infrastructure.persistence.migrations.JdbcMigrationStetemnets;
import com.hrrm.famoney.infrastructure.persistence.migrations.MigrationException;

@SuppressWarnings("java:S101")
public class V1M2EntryCategoriesJdbcStetements extends JdbcMigrationStetemnets {

    public V1M2EntryCategoriesJdbcStetements(final Connection connection, final ClassLoader classLoader) {
        super(connection,
            "migration-scripts/entry-categories",
            classLoader);
    }

    public PreparedStatement getCategoryInsertStatement() throws MigrationException {
        return getStatementWithGeneratedKeys("category_insert.sql");
    }

}

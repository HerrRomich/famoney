package com.hrrm.famoney.domain.datadirectory.migration;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.hrrm.famoney.infrastructure.persistence.migration.JdbcMigrationStetemnets;
import com.hrrm.famoney.infrastructure.persistence.migration.MigrationException;

public class V1_2_Entry_categoriesJdbcStetements extends JdbcMigrationStetemnets {

    public V1_2_Entry_categoriesJdbcStetements(final Connection connection, final ClassLoader classLoader) {
        super(connection,
            "/migration-scripts/entry-categories",
            classLoader);
    }

    public PreparedStatement getCategoryInsertStatement() throws MigrationException {
        return getStatementWithGeneratedKeys("category_insert.sql");
    }

}

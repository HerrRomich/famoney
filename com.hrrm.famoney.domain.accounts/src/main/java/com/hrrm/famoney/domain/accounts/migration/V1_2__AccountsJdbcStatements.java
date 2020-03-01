package com.hrrm.famoney.domain.accounts.migration;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.hrrm.famoney.infrastructure.persistence.migration.JdbcMigrationStetemnets;
import com.hrrm.famoney.infrastructure.persistence.migration.MigrationException;

public class V1_2__AccountsJdbcStatements extends JdbcMigrationStetemnets {

    public V1_2__AccountsJdbcStatements(final Connection connection, final ClassLoader classLoader) {
        super(connection,
            "/migration-scripts/entry-categories",
            classLoader);
    }

    public PreparedStatement getAccountInsert() throws MigrationException {
        return getStatementWithGeneratedKeys("account_insert.sql");
    }

    public PreparedStatement getAccountTagInsert() throws MigrationException {
        return getStatement("account_tag_insert.sql");
    }

}

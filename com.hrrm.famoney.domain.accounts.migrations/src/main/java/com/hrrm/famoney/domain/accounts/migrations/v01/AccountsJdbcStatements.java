package com.hrrm.famoney.domain.accounts.migrations.v01;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.hrrm.famoney.infrastructure.persistence.migrations.JdbcMigrationStetemnets;
import com.hrrm.famoney.infrastructure.persistence.migrations.MigrationException;

public class AccountsJdbcStatements extends JdbcMigrationStetemnets {

    public AccountsJdbcStatements(final Connection connection, final ClassLoader classLoader) {
        super(connection,
            "migration-scripts/accounts",
            classLoader);
    }

    public PreparedStatement getAccountInsert() throws MigrationException {
        return getStatementWithGeneratedKeys("account_insert.sql");
    }

    public PreparedStatement getAccountTagInsert() throws MigrationException {
        return getStatement("account_tag_insert.sql");
    }

}

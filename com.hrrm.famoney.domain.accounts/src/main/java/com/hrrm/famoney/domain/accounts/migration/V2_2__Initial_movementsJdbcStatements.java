package com.hrrm.famoney.domain.accounts.migration;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.hrrm.famoney.infrastructure.persistence.migration.JdbcMigrationStetemnets;
import com.hrrm.famoney.infrastructure.persistence.migration.MigrationException;

public class V2_2__Initial_movementsJdbcStatements extends JdbcMigrationStetemnets {

    public V2_2__Initial_movementsJdbcStatements(final Connection connection, final ClassLoader classLoader) {
        super(connection,
            "/migration-scripts/initial_movements",
            classLoader);
    }

    public PreparedStatement getAccountIdByNameSelect() throws MigrationException {
        return getStatement("account_name_by_id_select.sql");
    }

    public PreparedStatement getAccountMovementInsert() throws MigrationException {
        return getStatement("movement_insert.sql");
    }

    public PreparedStatement getAccountMovementsMinMaxDatesSelect() throws MigrationException {
        return getStatement("account_movements_min_max_dates_select");
    }

    public PreparedStatement getAccountsMovementsSumCountSelect() throws MigrationException {
        return getStatement("accounts_movements_sum_count_select.sql");
    }

    public PreparedStatement getAccountMovementsSumCountUpdate() throws MigrationException {
        return getStatement("account_movements_sum_count_update.sql");
    }

    public PreparedStatement getAccountMovementsSumCountBetweenMovementDatesSelect() throws MigrationException {
        return getStatement("account_movements_sum_count_between_movement_dates_select.sql");
    }

    public PreparedStatement getAccountMovementsSumCountBetweenBookingDatesSelect() throws MigrationException {
        return getStatement("account_movements_sum_count_between_booking_dates_select.sql");
    }

    public PreparedStatement getMovementSliceInsert() throws MigrationException {
        return getStatement("movement_slice_insert.sql");
    }

    public PreparedStatement getAccountMovementsMaxDateBetweenDatesSelect() throws MigrationException {
        return getStatement("account_movements_max_date_between_dates_select.sql");
    }

}

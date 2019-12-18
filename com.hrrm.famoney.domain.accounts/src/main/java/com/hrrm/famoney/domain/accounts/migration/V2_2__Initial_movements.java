package com.hrrm.famoney.domain.accounts.migration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import javax.json.Json;
import javax.json.JsonValue;
import javax.json.stream.JsonParser;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import com.hrrm.infrastructure.persistence.migration.MigrationException;

public class V2_2__Initial_movements extends BaseJavaMigration {

    private static final String INSERT_INTO_MOVEMENT_STATEMENT = "" +
            "insert into movement(account_id,\r\n" +
            "                     type,\r\n" +
            "                     date,\r\n" +
            "                     booking_date,\r\n" +
            "                     amount)\r\r" +
            "              values(?,\r\n" +
            "                     ?,\r\n" +
            "                     ?,\r\n" +
            "                     ?,\r\n" +
            "                     ?)";
    private static final String FIND_ACCOUNTID_BY_NAME = "" +
            "select a.id from account a\n\r" +
            "where a.name = ?";

    private static final String GET_ALL_ACCOUNT_MOVEMENT_SUM_AND_COUNT = "" +
            "select count(*)\r\n" +
            "     , sum(amount)\r\n" +
            "     , account_id\r\n" +
            "  from movement\r\n" +
            " group by account_id";

    private static final String UPDATE_ACCOUNT_MOVEMENT_SUM_AND_COUNT = "" +
            "update account a\r\n" +
            "  set a.movement_count = ?,\r\n" +
            "      a.movement_sum = ?\r\n" +
            " where a.id = ?";

    @Override
    public void migrate(Context context) throws Exception {
        try (final JsonParser parser = Json.createParser(this.getClass()
            .getResourceAsStream("V2_2__Initial_movements.json"));
                final var findAccountIdByNameStmt = context.getConnection()
                    .prepareStatement(FIND_ACCOUNTID_BY_NAME);
                final var insertMovementStmt = context.getConnection()
                    .prepareStatement(INSERT_INTO_MOVEMENT_STATEMENT)) {
            final var rootObject = parser.getObject();
            final var accounts = rootObject.getJsonObject("accounts");

            accounts.forEach((accountName, accountValue) -> {
                final var accountMovements = accountValue.asJsonArray();
                final var accountId = findAccountId(findAccountIdByNameStmt, accountName);
                accountMovements.forEach(movementValue -> insertMovement(insertMovementStmt,
                        accountId, movementValue));
            });
        }
        updateAccountMovemntsCountAndSum(context.getConnection());
    }

    private void insertMovement(final PreparedStatement insertMovementStmt, final int accountId,
            JsonValue movementValue) {
        try {
            final var movement = movementValue.asJsonObject();
            insertMovementStmt.setInt(1, accountId);
            insertMovementStmt.setString(2, "entry");
            final var dateAttribute = movement.getJsonString("date");
            var date = DateTimeFormatter.ISO_DATE_TIME.parse(dateAttribute.getString(),
                    LocalDateTime::from);
            insertMovementStmt.setTimestamp(3, Timestamp.valueOf(date));
            final var bookingDateAttribute = Optional.ofNullable(movement.getJsonString(
                    "bookingDate"));
            final var bookingDate = bookingDateAttribute.map(attr -> DateTimeFormatter.ISO_DATE_TIME
                .parse(attr.getString(), LocalDateTime::from))
                .orElse(date);
            insertMovementStmt.setTimestamp(4, Timestamp.valueOf(bookingDate));
            insertMovementStmt.setBigDecimal(5, movement.getJsonNumber("amount")
                .bigDecimalValue());
            insertMovementStmt.executeUpdate();
        } catch (DateTimeParseException | SQLException ex) {
            throw new MigrationException(ex);
        }
    }

    private int findAccountId(final PreparedStatement findAccountIdByNameStmt, String accountName) {
        try {
            findAccountIdByNameStmt.setString(1, accountName);
            findAccountIdByNameStmt.execute();
            return getAccountId(findAccountIdByNameStmt, accountName);
        } catch (SQLException ex) {
            throw new MigrationException(ex);
        }
    }

    private int getAccountId(final PreparedStatement findAccountIdByNameStmt, String accountName)
            throws SQLException {
        try (final var accountIds = findAccountIdByNameStmt.getResultSet()) {
            if (!accountIds.first()) {
                throw new MigrationException(MessageFormat.format(
                        "Account is not found by name: {0}", accountName));
            }
            return accountIds.getInt(1);
        }
    }

    private void updateAccountMovemntsCountAndSum(Connection conn) throws SQLException {
        try (final var getAllAccountsMovementsCountAndSumStmt = conn.prepareStatement(
                GET_ALL_ACCOUNT_MOVEMENT_SUM_AND_COUNT);
                final var updateAccountMovementsCountAndSumStmt = conn.prepareStatement(
                        UPDATE_ACCOUNT_MOVEMENT_SUM_AND_COUNT);
                final var countsAndSums = getAllAccountsMovementsCountAndSumStmt.executeQuery()) {
            while (countsAndSums.next()) {
                updateAccountMovementsCountAndSumStmt.setInt(1, countsAndSums.getInt(1));
                updateAccountMovementsCountAndSumStmt.setBigDecimal(2, countsAndSums.getBigDecimal(
                        2));
                updateAccountMovementsCountAndSumStmt.setInt(3, countsAndSums.getInt(3));
                updateAccountMovementsCountAndSumStmt.executeUpdate();
            }
        }

    }

}

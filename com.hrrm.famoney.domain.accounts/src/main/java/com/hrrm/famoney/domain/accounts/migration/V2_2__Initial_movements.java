package com.hrrm.famoney.domain.accounts.migration;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.TimeZone;

import javax.json.Json;
import javax.json.JsonValue;
import javax.json.stream.JsonParser;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import com.hrrm.famoney.infrastructure.persistence.migration.MigrationException;

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

    private static final String GET_MAX_ACCOUNT_MOVEMENT_DATE = "" +
            "select max(date)\r\n" +
            "  from movement\r\n" +
            " where date between ? and ?\r\n" +
            "   and account_id = ?";

    private static final String GET_MIN_MAX_ACCOUNT_DATES = "" +
            "select min(date)\r\n" +
            "     , max(date)\r\n" +
            "     , min(booking_date)\r\n" +
            "     , max(booking_date)\r\n" +
            "  from movement\r\n" +
            " where account_id = ?";

    private static final String GET_COUNT_AND_SUM_BETWEEN_MOVEMENT_DATES = "" +
            "select count(*)\r\n" +
            "     , sum(amount)\r\n" +
            "  from movement\r\n" +
            " where date between ? and ?\r\n" +
            "   and account_id = ?";

    private static final String GET_COUNT_AND_SUM_BETWEEN_BOOKING_DATES = "" +
            "select count(*)\r\n" +
            "     , sum(amount)\r\n" +
            "  from movement\r\n" +
            " where booking_date between ? and ?\r\n" +
            "   and account_id = ?";

    private static final String INSERT_MOVEMENT_SLICE = "" +
            "insert into movement_slice(account_id\r\n" +
            "                         , date\r\n" +
            "                         , movement_count\r\n" +
            "                         , movement_sum\r\n" +
            "                         , booking_count\r\n" +
            "                         , booking_sum)\r\n" +
            "values(?\r\n" +
            "     , ?\r\n" +
            "     , ?\r\n" +
            "     , ?\r\n" +
            "     , ?\r\n" +
            "     , ?)";

    @Override
    public void migrate(Context context) throws Exception {
        try (final JsonParser parser = Json.createParser(this.getClass()
            .getResourceAsStream("Pupsik Wallet.json"));
                final var findAccountIdByNameStmt = context.getConnection()
                    .prepareStatement(FIND_ACCOUNTID_BY_NAME);
                final var insertMovementStmt = context.getConnection()
                    .prepareStatement(INSERT_INTO_MOVEMENT_STATEMENT);
                final var selectMaxDateStmt = context.getConnection()
                    .prepareStatement(GET_MAX_ACCOUNT_MOVEMENT_DATE);
                final var selectMinMaxAccountDatesStmt = context.getConnection()
                    .prepareStatement(GET_MIN_MAX_ACCOUNT_DATES);
                final var selectCountAndSumBetweenMovementDatesStmt = context.getConnection()
                    .prepareStatement(GET_COUNT_AND_SUM_BETWEEN_MOVEMENT_DATES);
                final var selectCountAndSumBetweenBookingDatesStmt = context.getConnection()
                    .prepareStatement(GET_COUNT_AND_SUM_BETWEEN_BOOKING_DATES);
                final var insertMovementSliceStmt = context.getConnection()
                    .prepareStatement(INSERT_MOVEMENT_SLICE)) {
            final var rootObject = parser.getObject();
            final var accounts = rootObject.getJsonObject("accounts");

            accounts.forEach((accountName, accountValue) -> {
                final var accountMovements = accountValue.asJsonArray();
                final var accountId = findAccountId(findAccountIdByNameStmt, accountName);
                accountMovements.forEach(movementValue -> insertMovement(selectMaxDateStmt,
                        insertMovementStmt, accountId, movementValue, context.getConnection()));
                insertMovmentSlices(selectMinMaxAccountDatesStmt,
                        selectCountAndSumBetweenMovementDatesStmt,
                        selectCountAndSumBetweenBookingDatesStmt, insertMovementSliceStmt,
                        accountId, context.getConnection());
            });
        }
        updateAccountMovemntsCountAndSum(context.getConnection());
    }

    private void insertMovement(final PreparedStatement selectMaxDateStmt,
            final PreparedStatement insertMovementStmt, final int accountId,
            JsonValue movementValue, Connection connection) {
        try {
            final var movement = movementValue.asJsonObject();
            insertMovementStmt.setInt(1, accountId);
            insertMovementStmt.setString(2, "entry");
            final var dateAttribute = movement.getJsonString("date");
            var date = DateTimeFormatter.ISO_DATE.parse(dateAttribute.getString(), LocalDate::from);

            selectMaxDateStmt.setTimestamp(1, Timestamp.valueOf(date.atTime(0, 0)));
            selectMaxDateStmt.setTimestamp(2, Timestamp.valueOf(date.plusDays(1)
                .atTime(0, 0)));
            selectMaxDateStmt.setInt(3, accountId);
            LocalDateTime dateTime = null;
            try (final var resultSet = selectMaxDateStmt.executeQuery()) {
                Timestamp maxTimeStamp;
                if (resultSet.next() && (maxTimeStamp = resultSet.getTimestamp(1)) != null) {
                    dateTime = maxTimeStamp.toLocalDateTime()
                        .plusMinutes(5);
                } else {
                    dateTime = date.atTime(12, 0);
                }
            }
            insertMovementStmt.setTimestamp(3, Timestamp.valueOf(dateTime));
            final var bookingDateAttribute = Optional.ofNullable(movement.getJsonString(
                    "bookingDate"));
            final var bookingDate = bookingDateAttribute.map(attr -> DateTimeFormatter.ISO_DATE_TIME
                .parse(attr.getString(), LocalDateTime::from))
                .orElse(dateTime);
            insertMovementStmt.setTimestamp(4, Timestamp.valueOf(bookingDate));
            insertMovementStmt.setBigDecimal(5, movement.getJsonNumber("amount")
                .bigDecimalValue());
            insertMovementStmt.executeUpdate();
            connection.commit();
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

    private void insertMovmentSlices(PreparedStatement selectMinMaxAccountDatesStmt,
            PreparedStatement selectCountAndSumBetweenMovementDatesStmt,
            PreparedStatement selectCountAndSumBetweenBookingDatesStmt,
            PreparedStatement insertMovementSliceStmt, int accountId, Connection conn) {
        try {
            selectMinMaxAccountDatesStmt.setInt(1, accountId);
            selectCountAndSumBetweenMovementDatesStmt.setInt(3, accountId);
            selectCountAndSumBetweenBookingDatesStmt.setInt(3, accountId);
            insertMovementSliceStmt.setInt(1, accountId);
            try (final var resultSet = selectMinMaxAccountDatesStmt.executeQuery()) {
                if (!resultSet.next()) {
                    return;
                }
                final var minDateTimestamp = resultSet.getTimestamp(1);
                final var maxDateTimestamp = resultSet.getTimestamp(2);
                final var minBookingDateTimestamp = resultSet.getTimestamp(3);
                final var maxBookingDateTimestamp = resultSet.getTimestamp(4);
                if (minDateTimestamp == null ||
                        maxDateTimestamp == null ||
                        minBookingDateTimestamp == null ||
                        maxBookingDateTimestamp == null) {
                    return;
                }
                final var minTime = Math.min(minDateTimestamp.getTime(), minBookingDateTimestamp
                    .getTime());
                final var minDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(minTime), TimeZone
                    .getDefault()
                    .toZoneId())
                    .truncatedTo(ChronoUnit.DAYS);
                final var maxTime = Math.max(maxDateTimestamp.getTime(), maxBookingDateTimestamp
                    .getTime());
                final var maxDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(maxTime), TimeZone
                    .getDefault()
                    .toZoneId())
                    .truncatedTo(ChronoUnit.DAYS);
                var startDate = minDate.withDayOfMonth(1)
                    .plusMonths(1);
                var prevDate = minDate;
                var movementCount = 0;
                var movementSum = BigDecimal.ZERO;
                var bookingCount = 0;
                var bookingSum = BigDecimal.ZERO;
                while (startDate.compareTo(maxDate) < 0) {
                    selectCountAndSumBetweenMovementDatesStmt.setTimestamp(1, Timestamp.valueOf(
                            prevDate));
                    selectCountAndSumBetweenMovementDatesStmt.setTimestamp(2, Timestamp.valueOf(
                            startDate));
                    try (final var countAndSumByMovementDateResultSet = selectCountAndSumBetweenMovementDatesStmt
                        .executeQuery()) {
                        if (countAndSumByMovementDateResultSet.next()) {
                            movementCount += Optional.ofNullable(countAndSumByMovementDateResultSet
                                .getInt(1))
                                .orElse(0);
                            movementSum = movementSum.add(Optional.ofNullable(
                                    countAndSumByMovementDateResultSet.getBigDecimal(2))
                                .orElse(BigDecimal.ZERO));
                        }
                    }
                    selectCountAndSumBetweenBookingDatesStmt.setTimestamp(1, Timestamp.valueOf(
                            prevDate));
                    selectCountAndSumBetweenBookingDatesStmt.setTimestamp(2, Timestamp.valueOf(
                            startDate));
                    try (final var countAndSumByBookingDateResultSet = selectCountAndSumBetweenBookingDatesStmt
                        .executeQuery()) {
                        if (countAndSumByBookingDateResultSet.next()) {
                            bookingCount += Optional.ofNullable(countAndSumByBookingDateResultSet
                                .getInt(1))
                                .orElse(0);
                            bookingSum = bookingSum.add(Optional.ofNullable(
                                    countAndSumByBookingDateResultSet.getBigDecimal(2))
                                .orElse(BigDecimal.ZERO));
                        }
                    }
                    insertMovementSliceStmt.setTimestamp(2, Timestamp.valueOf(startDate));
                    insertMovementSliceStmt.setInt(3, movementCount);
                    insertMovementSliceStmt.setBigDecimal(4, movementSum);
                    insertMovementSliceStmt.setInt(5, bookingCount);
                    insertMovementSliceStmt.setBigDecimal(6, bookingSum);
                    insertMovementSliceStmt.execute();
                    prevDate = startDate;
                    startDate = startDate.plusMonths(1);
                }
            }
            conn.commit();
        } catch (SQLException e) {
            throw new MigrationException("Error adding movement slices.", e);
        }
    }

}

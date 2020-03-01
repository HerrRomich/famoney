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

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import com.hrrm.famoney.function.throwing.ThrowingBiConsumer;
import com.hrrm.famoney.function.throwing.ThrowingConsumer;
import com.hrrm.famoney.infrastructure.persistence.migration.MigrationException;

public class V2_2__Initial_movements extends BaseJavaMigration {

    @Override
    public void migrate(final Context context) throws MigrationException {
        try (final var jdbcStatements = new V2_2__Initial_movementsJdbcStatements(context.getConnection(),
            getClass().getClassLoader());
                final var parser = Json.createParser(this.getClass()
                    .getResourceAsStream("Pupsik Wallet.json"));) {
            final var rootObject = parser.getObject();
            final var accounts = rootObject.getJsonObject("accounts");

            accounts.forEach(ThrowingBiConsumer.sneaky((accountName, accountValue) -> {
                final var accountMovements = accountValue.asJsonArray();
                final var accountId = findAccountId(jdbcStatements.getAccountIdByNameSelect(),
                        accountName);
                accountMovements.forEach(ThrowingConsumer.sneaky(movementValue -> insertMovement(jdbcStatements,
                        accountId,
                        movementValue,
                        context.getConnection())));
                insertMovmentSlices(jdbcStatements,
                        accountId);
                context.getConnection()
                    .commit();

            }));
            updateAccountMovemntsCountAndSum(jdbcStatements);
        } catch (final SQLException e) {
            throw new MigrationException("It is unable to migrate account movements.",
                e);
        }
    }

    private void insertMovement(final V2_2__Initial_movementsJdbcStatements jdbcStatements, final int accountId,
            final JsonValue movementValue, final Connection connection) throws MigrationException {
        try {
            final var accountMovementsMaxDateBetweenDatesSelectStmt = jdbcStatements
                .getAccountMovementsMaxDateBetweenDatesSelect();
            final var accountMovementInsertStmt = jdbcStatements.getAccountMovementInsert();
            final var movement = movementValue.asJsonObject();
            accountMovementInsertStmt.setInt(1,
                    accountId);
            accountMovementInsertStmt.setString(2,
                    "entry");
            final var dateAttribute = movement.getJsonString("date");
            final var date = DateTimeFormatter.ISO_DATE.parse(dateAttribute.getString(),
                    LocalDate::from);

            accountMovementsMaxDateBetweenDatesSelectStmt.setTimestamp(1,
                    Timestamp.valueOf(date.atTime(0,
                            0)));
            accountMovementsMaxDateBetweenDatesSelectStmt.setTimestamp(2,
                    Timestamp.valueOf(date.plusDays(1)
                        .atTime(0,
                                0)));
            accountMovementsMaxDateBetweenDatesSelectStmt.setInt(3,
                    accountId);
            LocalDateTime dateTime = null;
            try (final var resultSet = accountMovementsMaxDateBetweenDatesSelectStmt.executeQuery()) {
                Timestamp maxTimeStamp;
                if (resultSet.next() && (maxTimeStamp = resultSet.getTimestamp(1)) != null) {
                    dateTime = maxTimeStamp.toLocalDateTime()
                        .plusMinutes(5);
                } else {
                    dateTime = date.atTime(12,
                            0);
                }
            }
            accountMovementInsertStmt.setTimestamp(3,
                    Timestamp.valueOf(dateTime));
            final var bookingDateAttribute = Optional.ofNullable(movement.getJsonString("bookingDate"));
            final var bookingDate = bookingDateAttribute.map(attr -> DateTimeFormatter.ISO_DATE_TIME.parse(attr
                .getString(),
                    LocalDateTime::from))
                .orElse(dateTime);
            accountMovementInsertStmt.setTimestamp(4,
                    Timestamp.valueOf(bookingDate));
            accountMovementInsertStmt.setBigDecimal(5,
                    movement.getJsonNumber("amount")
                        .bigDecimalValue());
            accountMovementInsertStmt.executeUpdate();
            connection.commit();
        } catch (DateTimeParseException | SQLException ex) {
            throw new MigrationException("It was unable to insert a movement.",
                ex);
        }
    }

    private int findAccountId(final PreparedStatement findAccountIdByNameStmt, final String accountName)
            throws MigrationException {
        try {
            findAccountIdByNameStmt.setString(1,
                    accountName);
            findAccountIdByNameStmt.execute();
            return getAccountId(findAccountIdByNameStmt,
                    accountName);
        } catch (final SQLException ex) {
            throw new MigrationException(ex);
        }
    }

    private int getAccountId(final PreparedStatement findAccountIdByNameStmt, final String accountName)
            throws MigrationException {
        try (final var accountIds = findAccountIdByNameStmt.getResultSet()) {
            if (!accountIds.first()) {
                throw new MigrationException(MessageFormat.format("Account by name: {0} is not found.",
                        accountName));
            }
            return accountIds.getInt(1);
        } catch (final SQLException e) {
            throw new MigrationException("Cant get account id.",
                e);
        }
    }

    private void updateAccountMovemntsCountAndSum(final V2_2__Initial_movementsJdbcStatements jdbcStatements)
            throws MigrationException {
        final var accountsMovementsSumCountSelectStmt = jdbcStatements.getAccountsMovementsSumCountSelect();
        final var accountMovementsSumCountUpdateStmt = jdbcStatements.getAccountMovementsSumCountUpdate();
        try (final var countsAndSums = accountsMovementsSumCountSelectStmt.executeQuery()) {
            while (countsAndSums.next()) {
                accountMovementsSumCountUpdateStmt.setInt(1,
                        countsAndSums.getInt(1));
                accountMovementsSumCountUpdateStmt.setBigDecimal(2,
                        countsAndSums.getBigDecimal(2));
                accountMovementsSumCountUpdateStmt.setInt(3,
                        countsAndSums.getInt(3));
                accountMovementsSumCountUpdateStmt.executeUpdate();
            }
        } catch (final SQLException e) {
            throw new MigrationException("Cannot update account movements counts and sums.",
                e);
        }

    }

    private void insertMovmentSlices(final V2_2__Initial_movementsJdbcStatements jdbcStatements, final int accountId)
            throws MigrationException {
        try {
            final var accountMovementsMinMaxDatesSelectStmt = jdbcStatements.getAccountMovementsMinMaxDatesSelect();
            final PreparedStatement accountMovementsSumCountBetweenMovementDatesSelectStmt = jdbcStatements
                .getAccountMovementsSumCountBetweenMovementDatesSelect();
            final PreparedStatement accountMovementsSumCountBetweenBookingDatesSelectStmt = jdbcStatements
                .getAccountMovementsSumCountBetweenBookingDatesSelect();
            final PreparedStatement insertMovementSliceStmt = jdbcStatements.getMovementSliceInsert();

            accountMovementsMinMaxDatesSelectStmt.setInt(1,
                    accountId);
            accountMovementsSumCountBetweenMovementDatesSelectStmt.setInt(3,
                    accountId);
            accountMovementsSumCountBetweenBookingDatesSelectStmt.setInt(3,
                    accountId);
            insertMovementSliceStmt.setInt(1,
                    accountId);
            try (final var resultSet = accountMovementsMinMaxDatesSelectStmt.executeQuery()) {
                if (!resultSet.next()) {
                    return;
                }
                final var minDateTimestamp = resultSet.getTimestamp(1);
                final var maxDateTimestamp = resultSet.getTimestamp(2);
                final var minBookingDateTimestamp = resultSet.getTimestamp(3);
                final var maxBookingDateTimestamp = resultSet.getTimestamp(4);
                if (
                    minDateTimestamp == null ||
                        maxDateTimestamp == null ||
                        minBookingDateTimestamp == null ||
                        maxBookingDateTimestamp == null
                ) {
                    return;
                }
                final var minTime = Math.min(minDateTimestamp.getTime(),
                        minBookingDateTimestamp.getTime());
                final var minDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(minTime),
                        TimeZone.getDefault()
                            .toZoneId())
                    .truncatedTo(ChronoUnit.DAYS);
                final var maxTime = Math.max(maxDateTimestamp.getTime(),
                        maxBookingDateTimestamp.getTime());
                final var maxDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(maxTime),
                        TimeZone.getDefault()
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
                    accountMovementsSumCountBetweenMovementDatesSelectStmt.setTimestamp(1,
                            Timestamp.valueOf(prevDate));
                    accountMovementsSumCountBetweenMovementDatesSelectStmt.setTimestamp(2,
                            Timestamp.valueOf(startDate));
                    try (final var countAndSumByMovementDateResultSet = accountMovementsSumCountBetweenMovementDatesSelectStmt
                        .executeQuery()) {
                        if (countAndSumByMovementDateResultSet.next()) {
                            movementCount += Optional.ofNullable(countAndSumByMovementDateResultSet.getInt(1))
                                .orElse(0);
                            movementSum = movementSum.add(Optional.ofNullable(countAndSumByMovementDateResultSet
                                .getBigDecimal(2))
                                .orElse(BigDecimal.ZERO));
                        }
                    }
                    accountMovementsSumCountBetweenBookingDatesSelectStmt.setTimestamp(1,
                            Timestamp.valueOf(prevDate));
                    accountMovementsSumCountBetweenBookingDatesSelectStmt.setTimestamp(2,
                            Timestamp.valueOf(startDate));
                    try (final var countAndSumByBookingDateResultSet = accountMovementsSumCountBetweenBookingDatesSelectStmt
                        .executeQuery()) {
                        if (countAndSumByBookingDateResultSet.next()) {
                            bookingCount += Optional.ofNullable(countAndSumByBookingDateResultSet.getInt(1))
                                .orElse(0);
                            bookingSum = bookingSum.add(Optional.ofNullable(countAndSumByBookingDateResultSet
                                .getBigDecimal(2))
                                .orElse(BigDecimal.ZERO));
                        }
                    }
                    insertMovementSliceStmt.setTimestamp(2,
                            Timestamp.valueOf(startDate));
                    insertMovementSliceStmt.setInt(3,
                            movementCount);
                    insertMovementSliceStmt.setBigDecimal(4,
                            movementSum);
                    insertMovementSliceStmt.setInt(5,
                            bookingCount);
                    insertMovementSliceStmt.setBigDecimal(6,
                            bookingSum);
                    insertMovementSliceStmt.execute();
                    prevDate = startDate;
                    startDate = startDate.plusMonths(1);
                }
            }
        } catch (final SQLException e) {
            throw new MigrationException("Error adding movement slices.",
                e);
        }
    }

}

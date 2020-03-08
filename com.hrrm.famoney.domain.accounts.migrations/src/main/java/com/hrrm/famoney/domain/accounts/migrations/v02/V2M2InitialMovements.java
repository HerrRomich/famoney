package com.hrrm.famoney.domain.accounts.migrations.v02;

import java.math.BigDecimal;
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

import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.migration.Context;
import org.flywaydb.core.api.migration.JavaMigration;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import com.hrrm.famoney.domain.accounts.migrations.v01.V1M2Accounts;
import com.hrrm.famoney.function.throwing.ThrowingBiConsumer;
import com.hrrm.famoney.function.throwing.ThrowingConsumer;
import com.hrrm.famoney.function.throwing.ThrowingSupplier;
import com.hrrm.famoney.infrastructure.persistence.migrations.MigrationException;

public class V2M2InitialMovements implements JavaMigration {

    private final Logger logger;

    public V2M2InitialMovements(final LoggerFactory loggerFactory) {
        super();
        this.logger = loggerFactory.getLogger(V1M2Accounts.class);
    }

    @Override
    public MigrationVersion getVersion() {
        return MigrationVersion.fromVersion("02.02");
    }

    @Override
    public String getDescription() {
        return "Initial account movements";
    }

    @Override
    public Integer getChecksum() {
        return 0xa1b14a2c;
    }

    @Override
    public boolean isUndo() {
        return false;
    }

    @Override
    public boolean canExecuteInTransaction() {
        return true;
    }

    @Override
    public void migrate(final Context context) throws MigrationException {
        logger.info("Starting migration: \"{} {}\".",
                getVersion(),
                getDescription());
        try (final var jdbcStatements = new InitialMovementsJdbcStatements(context.getConnection(),
            getClass().getClassLoader())) {
            insertAccountsMovements(jdbcStatements);
            updateAccountsMovemntsCountAndSum(jdbcStatements);
            logger.info("Migration: \"{} {}\" is successfully completed.",
                    getVersion(),
                    getDescription());
        } catch (final SQLException | MigrationException e) {
            final var message = MessageFormat.format("It is unable to perform migration: \"{0} {1}\".",
                    getVersion(),
                    getDescription());
            logger.error(message,
                    e);
            throw new MigrationException(message,
                e);
        }
    }

    private void insertAccountsMovements(final InitialMovementsJdbcStatements jdbcStatements) {
        logger.info("Inserting initial movements.");
        try (final var parser = Json.createParser(this.getClass()
            .getResourceAsStream("Pupsik_Wallet.json"))) {
            final var rootObject = parser.getObject();
            final var accounts = rootObject.getJsonObject("accounts");
            accounts.forEach(ThrowingBiConsumer.sneaky((accountName, accountValue) -> insertAccountMovements(
                    jdbcStatements,
                    accountName,
                    accountValue)));
            logger.info("Initial movements successfully inserted.");
        }
    }

    private void insertAccountMovements(final InitialMovementsJdbcStatements jdbcStatements, final String accountName,
            final JsonValue accountValue) throws MigrationException {
        logger.info("Inserting initial movements for account: \"{}\".",
                accountName);
        try {
            final var accountMovements = accountValue.asJsonArray();
            final var accountId = findAccountId(jdbcStatements.getAccountIdByNameSelect(),
                    accountName);
            accountMovements.forEach(ThrowingConsumer.sneaky(movementValue -> {
                final AccountMovementData accountMovementData = getAccountMovementData(jdbcStatements,
                        accountId,
                        movementValue);
                insertMovement(jdbcStatements,
                        accountMovementData);
            }));
            insertMovmentSlices(jdbcStatements,
                    accountId);
            jdbcStatements.getConnection()
                .commit();
            logger.info("Initial movements for account: \"{}\" are successfully inserted.",
                    accountName);
            logger.debug(l -> l.debug("{} initial movements for account: \"{}\" are successfully inserted.",
                    accountMovements.size(),
                    accountName));
        } catch (MigrationException | SQLException e) {
            final var message = MessageFormat.format("It is unable to insert movements for account: \"{0}\"",
                    accountName);
            logger.error(message,
                    e);
            throw new MigrationException(message,
                e);
        }
    }

    private AccountMovementData getAccountMovementData(final InitialMovementsJdbcStatements jdbcStatements,
            final int accountId, final JsonValue movementValue) throws MigrationException {
        logger.trace("Combining data for account movement.");
        try {
            final var movement = movementValue.asJsonObject();
            var accountMovementDataBuilder = AccountMovementDataImpl.builder()
                .accountId(accountId);
            final var date = DateTimeFormatter.ISO_DATE.parse(movement.getJsonString("date")
                .getString(),
                    LocalDate::from);
            accountMovementDataBuilder = accountMovementDataBuilder.date(findNextDate(jdbcStatements,
                    accountId,
                    date));

            final var bookingDateAttribute = Optional.ofNullable(movement.getJsonString("bookingDate"));
            final var bookingDate = bookingDateAttribute.map(attr -> DateTimeFormatter.ISO_DATE_TIME.parse(attr
                .getString(),
                    LocalDateTime::from))
                .orElseGet(ThrowingSupplier.sneaky(() -> findNextDate(jdbcStatements,
                        accountId,
                        date)));
            accountMovementDataBuilder = accountMovementDataBuilder.bookingDate(bookingDate);
            accountMovementDataBuilder = accountMovementDataBuilder.amount(movement.getJsonNumber("amount")
                .bigDecimalValue());
            final var accountMovementData = accountMovementDataBuilder.build();
            logger.trace(l -> l.trace("Combined data for account movement: {}",
                    accountMovementData));
            return accountMovementData;
        } catch (final MigrationException e) {
            final var message = "Couldn't combine data for account movement.";
            logger.error(message);
            throw new MigrationException(message,
                e);
        }
    }

    private LocalDateTime findNextDate(final InitialMovementsJdbcStatements jdbcStatements, final int accountId,
            final LocalDate date) throws MigrationException {
        logger.trace(l -> logger.trace("Searching for next free timestamp for account id: {} on date.",
                accountId,
                date));
        try {
            final var accountMovementsMaxDateBetweenDatesSelectStmt = jdbcStatements
                .getAccountMovementsMaxDateBetweenDatesSelect();
            accountMovementsMaxDateBetweenDatesSelectStmt.setTimestamp(1,
                    Timestamp.valueOf(date.atStartOfDay()));
            accountMovementsMaxDateBetweenDatesSelectStmt.setTimestamp(2,
                    Timestamp.valueOf(date.plusDays(1)
                        .atStartOfDay()));
            accountMovementsMaxDateBetweenDatesSelectStmt.setInt(3,
                    accountId);
            LocalDateTime dateTime = null;
            try (final var resultSet = accountMovementsMaxDateBetweenDatesSelectStmt.executeQuery()) {
                final var maxTimeStamp = resultSet.next() ? Optional.ofNullable(resultSet.getTimestamp(1))
                        : Optional.<Timestamp>empty();
                dateTime = maxTimeStamp.map(ts -> ts.toLocalDateTime()
                    .plusMinutes(5))
                    .orElse(date.atTime(12,
                            0));
            }
            logger.trace(l -> logger.trace("Next free timestamp for account id: {} is {}.",
                    accountId,
                    date));
            return dateTime;
        } catch (MigrationException | SQLException e) {
            final var message = MessageFormat.format(
                    "It is unbale to find next free timestamp for account id: {0} on {1}.",
                    accountId,
                    date);
            logger.error(message,
                    e);
            throw new MigrationException(message,
                e);
        }
    }

    private void insertMovement(final InitialMovementsJdbcStatements jdbcStatements,
            final AccountMovementData accountMovementData) throws MigrationException {
        logger.trace(l -> l.trace("Inserting account movement: {}.",
                accountMovementData));
        try {
            final var accountMovementInsertStmt = jdbcStatements.getAccountMovementInsert();

            accountMovementInsertStmt.setInt(1,
                    accountMovementData.getAccountId());
            accountMovementInsertStmt.setString(2,
                    "entry");
            accountMovementInsertStmt.setTimestamp(3,
                    Timestamp.valueOf(accountMovementData.getDate()));

            accountMovementInsertStmt.setTimestamp(3,
                    Timestamp.valueOf(accountMovementData.getDate()));
            accountMovementInsertStmt.setTimestamp(4,
                    Timestamp.valueOf(accountMovementData.getBookingDate()));
            accountMovementInsertStmt.setBigDecimal(5,
                    accountMovementData.getAmount());
            accountMovementInsertStmt.executeUpdate();
            logger.trace("Account movement is successfully inserted.");
        } catch (DateTimeParseException | SQLException ex) {
            final String message = MessageFormat.format("It was unable to insert an account movement for data: {}.",
                    accountMovementData);
            logger.error(message);
            throw new MigrationException(message,
                ex);
        }
    }

    private int findAccountId(final PreparedStatement findAccountIdByNameStmt, final String accountName)
            throws MigrationException {
        logger.debug(l -> l.debug("Searching for account with name: {}.",
                accountName));
        try {
            findAccountIdByNameStmt.setString(1,
                    accountName);
            findAccountIdByNameStmt.execute();
            final var accountId = getAccountId(findAccountIdByNameStmt,
                    accountName);
            logger.debug(l -> l.debug("Account with name: {} is found.",
                    accountName));
            logger.trace(l -> l.debug("Account with name: {} is found under id: {}.",
                    accountName,
                    accountId));
            return accountId;
        } catch (final SQLException e) {
            final var message = MessageFormat.format("Cant find account id by name: {0}.",
                    accountName);
            logger.error(message);
            throw new MigrationException(message,
                e);
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
            throw new MigrationException("Cant extract account id.",
                e);
        }
    }

    private void updateAccountsMovemntsCountAndSum(final InitialMovementsJdbcStatements jdbcStatements)
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

    private void insertMovmentSlices(final InitialMovementsJdbcStatements jdbcStatements, final int accountId)
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

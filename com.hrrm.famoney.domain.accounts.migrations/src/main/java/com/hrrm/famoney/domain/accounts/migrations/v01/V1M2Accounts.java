package com.hrrm.famoney.domain.accounts.migrations.v01;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.json.Json;
import javax.json.JsonString;
import javax.json.stream.JsonParser;

import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.migration.Context;
import org.flywaydb.core.api.migration.JavaMigration;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import com.hrrm.famoney.function.throwing.ThrowingConsumer;
import com.hrrm.famoney.infrastructure.persistence.migrations.MigrationException;

public class V1M2Accounts implements JavaMigration {

    private static final int DEFAULT_BUDGET_ID = 1;

    private final Logger logger;

    public V1M2Accounts(final LoggerFactory loggerFactory) {
        super();
        this.logger = loggerFactory.getLogger(V1M2Accounts.class);
    }

    @Override
    public MigrationVersion getVersion() {
        return MigrationVersion.fromVersion("01.02");
    }

    @Override
    public String getDescription() {
        return "Creates data of test accounts.";
    }

    @Override
    public Integer getChecksum() {
        return 0x4f673c5f;
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
        logger.info("Starting migration: \"01.01 Accounts\"");
        try (final var jdbcStatements = new AccountsJdbcStatements(context.getConnection(),
            this.getClass()
                .getClassLoader());
                final JsonParser parser = Json.createParser(this.getClass()
                    .getResourceAsStream("V1M2Accounts.json"));) {
            final var rootJsonObject = parser.getObject();
            final var accountsJsonArray = rootJsonObject.getJsonArray("accounts");

            accountsJsonArray.forEach(ThrowingConsumer.sneaky(accountJsonValue -> {
                final var accountJsonObject = accountJsonValue.asJsonObject();
                final var accountData = AccountDataImpl.builder()
                    .budgetId(DEFAULT_BUDGET_ID)
                    .name(accountJsonObject.getString("name"))
                    .openDate(DateTimeFormatter.ISO_DATE.parse(accountJsonObject.getString("openDate"),
                            LocalDate::from))
                    .tags(accountJsonObject.getJsonArray("tags")
                        .getValuesAs(JsonString::getString))
                    .build();

                insertAccountData(jdbcStatements,
                        accountData);
            }));
            logger.info("Migration: \"01.01 Accounts\" is successfully completed.");
        } catch (final SQLException e) {
            throw new MigrationException("Couldn't close opend exceptions.");
        } catch (final Exception e) {
            logger.error("Migration: \"01.01 Accounts\" is failed.",
                    e);
            throw e;
        }
    }

    private void insertAccountData(final AccountsJdbcStatements jdbcStatements, final AccountData accountData)
            throws MigrationException {
        logger.debug("Inserting account data for account: \"{}\".",
                accountData.getName());
        logger.trace(l -> l.trace("Inserting account data: {}.",
                accountData));
        final var accountId = insertAccount(jdbcStatements.getAccountInsert(),
                accountData);
        accountData.getTags()
            .forEach(ThrowingConsumer.sneaky(accountTag -> insertAccountTag(jdbcStatements.getAccountTagInsert(),
                    accountId,
                    accountTag)));
        logger.debug("Account data for account: \"{}\" is successfully inserted.",
                accountData.getName());
        logger.trace(l -> l.trace("Inserting account data: {}.",
                accountData));
    }

    private Integer insertAccount(final PreparedStatement stmt, final AccountData accountData)
            throws MigrationException {
        logger.debug("Inserting account: \"{}\".",
                accountData.getName());
        try (final var generatedKeys = callInsertAccount(stmt,
                accountData)) {
            logger.debug("Account: \"{}\" is successfully inserted.",
                    accountData.getName());
            if (generatedKeys.next()) {
                final int accountId = generatedKeys.getInt(1);
                logger.trace("Account: \"{}\" is successfully inserted with id: {}.",
                        accountData.getName(),
                        accountId);
                return accountId;
            }
            return null;
        } catch (final SQLException e) {
            logger.error("Account: \"{}\" couldn't be inserted.",
                    accountData.getName(),
                    e);
            throw new MigrationException(e);
        }
    }

    private ResultSet callInsertAccount(final PreparedStatement stmt, final AccountData accountData)
            throws SQLException {
        stmt.setInt(1,
                1);
        stmt.setDate(2,
                Date.valueOf(accountData.getOpenDate()));
        stmt.setString(3,
                accountData.getName());
        stmt.executeUpdate();
        return stmt.getGeneratedKeys();
    }

    private void insertAccountTag(final PreparedStatement stmt, final Integer accountId, final String accountTag)
            throws MigrationException {
        logger.debug("Inserting tag: {} into account with id: {}.",
                accountTag,
                accountId);
        try {
            stmt.setInt(1,
                    accountId);
            stmt.setString(2,
                    accountTag);
            stmt.executeUpdate();
            logger.debug("Tag: {} is successfully inserted into account with id: {}.",
                    accountTag,
                    accountId);
        } catch (final SQLException e) {
            logger.debug("Tag: {} couldn't be inserted into account with id: {}.",
                    accountTag,
                    accountId,
                    e);
            throw new MigrationException(e);
        }
    }

}

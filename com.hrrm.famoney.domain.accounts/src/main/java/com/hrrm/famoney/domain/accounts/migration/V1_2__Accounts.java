package com.hrrm.famoney.domain.accounts.migration;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.json.Json;
import javax.json.JsonString;
import javax.json.stream.JsonParser;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import com.hrrm.famoney.function.throwing.ThrowingConsumer;
import com.hrrm.famoney.infrastructure.persistence.migration.MigrationException;

public class V1_2__Accounts extends BaseJavaMigration {

    @Override
    public void migrate(final Context context) throws MigrationException {
        try (final var jdbcStatements = new V1_2__AccountsJdbcStatements(context.getConnection(),
            this.getClass()
                .getClassLoader());
                final JsonParser parser = Json.createParser(this.getClass()
                    .getResourceAsStream("V1_2__Accounts.json"));) {
            final var rootJsonObject = parser.getObject();
            final var accountsJsonArray = rootJsonObject.getJsonArray("accounts");

            accountsJsonArray.forEach(ThrowingConsumer.sneaky(accountJsonValue -> {
                final var accountJsonObject = accountJsonValue.asJsonObject();
                final var accountId = insertAccount(jdbcStatements.getAccountInsert(),
                        DateTimeFormatter.ISO_DATE.parse(accountJsonObject.getString("openDate"),
                                LocalDate::from)
                            .atStartOfDay(),
                        accountJsonObject.getString("name"));
                accountJsonObject.getJsonArray("tags")
                    .getValuesAs(JsonString.class)
                    .forEach(ThrowingConsumer.sneaky(accountTagValue -> insertAccountTag(jdbcStatements
                        .getAccountTagInsert(),
                            accountId,
                            accountTagValue.getString())));
            }));
        } catch (final SQLException e) {
            throw new MigrationException("Couldn't close opend exceptions.");
        }
    }

    private void insertAccountTag(final PreparedStatement stmt, final Integer accountId, final String accountTag)
            throws MigrationException {
        try {
            stmt.setInt(1,
                    accountId);
            stmt.setString(2,
                    accountTag);
            stmt.executeUpdate();
        } catch (final SQLException e) {
            throw new MigrationException(e);
        }
    }

    private Integer insertAccount(final PreparedStatement stmt, final LocalDateTime openDate, final String name)
            throws MigrationException {
        try (final var generatedKeys = callInsertAccount(stmt,
                openDate,
                name)) {
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
            return null;
        } catch (final SQLException e) {
            throw new MigrationException(e);
        }
    }

    private ResultSet callInsertAccount(final PreparedStatement stmt, final LocalDateTime openDate, final String name)
            throws SQLException {
        stmt.setInt(1,
                1);
        stmt.setTimestamp(2,
                Timestamp.valueOf(openDate));
        stmt.setString(3,
                name);
        stmt.executeUpdate();
        return stmt.getGeneratedKeys();
    }

}

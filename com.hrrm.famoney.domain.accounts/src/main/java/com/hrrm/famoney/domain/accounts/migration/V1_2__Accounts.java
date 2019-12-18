package com.hrrm.famoney.domain.accounts.migration;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.stream.JsonParser;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import com.hrrm.infrastructure.persistence.migration.MigrationException;

public class V1_2__Accounts extends BaseJavaMigration {
    private static final String INSERT_INTO_ACCOUNT_STATEMENT = "" +
            "insert into account(\r\n" +
            "                    budget_id,\r\n" +
            "                    open_date,\r\n" +
            "                    name,\r\n" +
            "                    movement_count,\r\n" +
            "                    movement_sum\r\n" +
            "                   )\r\n" +
            "             values(\r\n" +
            "                    ?,\r\n" +
            "                    ?,\r\n" +
            "                    ?,\r\n" +
            "                    0,\r\n" +
            "                    0\r\n" +
            "                   )";

    private static final String INSERT_INTO_ACCOUNT_TAG_STATEMENT = "" +
            "insert into account_tag(\r\n" +
            "                        account_id,\r\n" +
            "                        tag\r\n" +
            "                       )\r\n" +
            "                 values(\r\n" +
            "                        ?,\r\n" +
            "                        ?\r\n" +
            "                       )";

    @Override
    public void migrate(Context context) throws Exception {
        try (final JsonParser parser = Json.createParser(this.getClass()
            .getResourceAsStream("V1_2__Accounts.json"));
                final PreparedStatement insertAccountStmt = context.getConnection()
                    .prepareStatement(INSERT_INTO_ACCOUNT_STATEMENT, new String[] { "id" });
                final PreparedStatement insertTagStmt = context.getConnection()
                    .prepareStatement(INSERT_INTO_ACCOUNT_TAG_STATEMENT)) {
            final JsonObject rootJsonObject = parser.getObject();
            final JsonArray accountsJsonArray = rootJsonObject.getJsonArray("accounts");

            accountsJsonArray.forEach(accountJsonValue -> {
                final JsonObject accountJsonObject = accountJsonValue.asJsonObject();
                final Integer accountId = insertAccount(insertAccountStmt,
                        DateTimeFormatter.ISO_DATE.parse(accountJsonObject.getString("openDate"),
                                LocalDate::from)
                            .atStartOfDay(), accountJsonObject.getString("name"));
                accountJsonObject.getJsonArray("tags")
                    .getValuesAs(JsonString.class)
                    .forEach(accountTagValue -> insertAccountTag(insertTagStmt, accountId,
                            accountTagValue.getString()));
            });
        }
    }

    private void insertAccountTag(final PreparedStatement stmt, final Integer accountId,
            final String accountTag) {
        try {
            stmt.setInt(1, accountId);
            stmt.setString(2, accountTag);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new MigrationException(e);
        }
    }

    private Integer insertAccount(PreparedStatement stmt, LocalDateTime openDate, String name) {
        try (final ResultSet generatedKeys = callInsertAccount(stmt, openDate, name)) {
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
            return null;
        } catch (SQLException e) {
            throw new MigrationException(e);
        }
    }

    private ResultSet callInsertAccount(PreparedStatement stmt, LocalDateTime openDate, String name)
            throws SQLException {
        stmt.setInt(1, 1);
        stmt.setTimestamp(2, Timestamp.valueOf(openDate));
        stmt.setString(3, name);
        stmt.executeUpdate();
        return stmt.getGeneratedKeys();
    }

}

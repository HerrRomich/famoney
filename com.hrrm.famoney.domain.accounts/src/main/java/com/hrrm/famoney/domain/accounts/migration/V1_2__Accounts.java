package com.hrrm.famoney.domain.accounts.migration;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.stream.JsonParser;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

public class V1_2__Accounts extends BaseJavaMigration {

    private static final String INSERT_INTO_ACCOUNT_STAEMENT = "" +
            "insert into account(\r\n" +
            "                    budget_id,\r\n" +
            "                    name\r\n" +
            "                   )\r\n" +
            "             values(\r\n" +
            "                    ?,\r\n" +
            "                    ?\r\n" +
            "                   )";

    private static final String INSERT_INTO_ACCOUNT_TAG_STAEMENT = "" +
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
                    .prepareStatement(INSERT_INTO_ACCOUNT_STAEMENT, new String[] { "id" });
                final PreparedStatement insertTagStmt = context.getConnection()
                    .prepareStatement(INSERT_INTO_ACCOUNT_TAG_STAEMENT)) {
            final JsonObject rootJsonObject = parser.getObject();
            final JsonArray accountsJsonArray = rootJsonObject.getJsonArray("accounts");

            accountsJsonArray.forEach(accountJsonValue -> {
                final JsonObject accountJsonObject = accountJsonValue.asJsonObject();
                final Integer accountId = insertAccount(insertAccountStmt, accountJsonObject.getString("name"));
                accountJsonObject.getJsonArray("tags")
                    .getValuesAs(JsonString.class)
                    .forEach(accountTagValue -> insertAccountTag(insertTagStmt, accountId, accountTagValue
                        .getString()));
            });
        }
    }

    private void insertAccountTag(final PreparedStatement stmt, final Integer accountId, final String accountTag) {
        try {
            stmt.setInt(1, accountId);
            stmt.setString(2, accountTag);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Integer insertAccount(PreparedStatement stmt, String name) {
        try {
            stmt.setInt(1, 1);
            stmt.setString(2, name);
            stmt.executeUpdate();
            final ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

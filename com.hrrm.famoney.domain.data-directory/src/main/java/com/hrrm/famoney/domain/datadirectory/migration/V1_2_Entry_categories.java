package com.hrrm.famoney.domain.datadirectory.migration;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.OptionalInt;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.stream.JsonParser;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import com.hrrm.famoney.domain.datadirectory.migration.CategoryDataImpl;
import com.hrrm.famoney.domain.datadirectory.migration.CategoryGroupDataImpl;
import com.hrrm.famoney.function.throwing.ThrowingConsumer;
import com.hrrm.famoney.function.throwing.ThrowingIntConsumer;
import com.hrrm.famoney.function.throwing.ThrowingRunnable;
import com.hrrm.famoney.infrastructure.persistence.migration.MigrationException;

public class V1_2_Entry_categories extends BaseJavaMigration {

    private static final int BUDGET_ID = 1;

    @Override
    public void migrate(final Context context) throws MigrationException {
        try (final var jdbcStatements = new V1_2_Entry_categoriesJdbcStetements(context.getConnection(),
            this.getClass()
                .getClassLoader());
                final JsonParser parser = Json.createParser(this.getClass()
                    .getResourceAsStream("initial_entry_categories.json"))) {
            final var rootJsonObject = parser.getObject();
            final var incomeCategoriesJsonArray = rootJsonObject.getJsonArray("incomes");
            addCategories(CategoryGroupDataImpl.builder()
                .type("income")
                .parentId(OptionalInt.empty())
                .build(),
                    incomeCategoriesJsonArray,
                    jdbcStatements);
            final var expenseCategoriesJsonArray = rootJsonObject.getJsonArray("expenses");
            addCategories(CategoryGroupDataImpl.builder()
                .type("expense")
                .parentId(OptionalInt.empty())
                .build(),
                    expenseCategoriesJsonArray,
                    jdbcStatements);

        } catch (final SQLException e) {
            throw new MigrationException("Couldn't close opend exceptions.");
        }

    }

    private void addCategories(final CategoryGroupData categoryGroupData, final JsonArray categories,
            final V1_2_Entry_categoriesJdbcStetements jdbcStatements) {
        categories.forEach(ThrowingConsumer.sneaky(categoryJsonValue -> {
            final JsonObject categoryJsonObject = categoryJsonValue.asJsonObject();
            final var categoryData = CategoryDataImpl.builder()
                .from(categoryGroupData)
                .name(categoryJsonObject.getString("name"))
                .build();
            final var categoryId = insertCategory(categoryData,
                    jdbcStatements.getCategoryInsertStatement());
            addCategories(CategoryGroupDataImpl.builder()
                .from(categoryGroupData)
                .parentId(categoryId)
                .build(),
                    categoryJsonObject.getJsonArray("children"),
                    jdbcStatements);
        }));
    }

    private int insertCategory(final CategoryData categoryData, final PreparedStatement stmt)
            throws MigrationException {
        try (final ResultSet generatedKeys = callInsertAccount(stmt,
                categoryData)) {
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
            throw new MigrationException("Unable to get ID from inserted category");
        } catch (final SQLException e) {
            throw new MigrationException(e);
        }
    }

    private ResultSet callInsertAccount(final PreparedStatement stmt, final CategoryData categoryData)
            throws SQLException {
        stmt.setString(1,
                categoryData.getType());
        stmt.setInt(2,
                BUDGET_ID);
        stmt.setString(3,
                categoryData.getName());
        categoryData.getParentId()
            .ifPresentOrElse(ThrowingIntConsumer.sneaky(parentId -> stmt.setInt(4,
                    parentId)),
                    ThrowingRunnable.sneaky(() -> stmt.setNull(4,
                            Types.INTEGER)));
        stmt.executeUpdate();
        return stmt.getGeneratedKeys();
    }

}

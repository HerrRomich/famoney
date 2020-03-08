package com.hrrm.famoney.domain.datadirectory.migrations.v1;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.OptionalInt;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.stream.JsonParser;

import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.migration.Context;
import org.flywaydb.core.api.migration.JavaMigration;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import com.hrrm.famoney.function.throwing.ThrowingConsumer;
import com.hrrm.famoney.function.throwing.ThrowingIntConsumer;
import com.hrrm.famoney.function.throwing.ThrowingRunnable;
import com.hrrm.famoney.infrastructure.persistence.migrations.MigrationException;

public class V1M2EntryCategories implements JavaMigration {

    private static final int BUDGET_ID = 1;

    private final Logger logger;

    public V1M2EntryCategories(final LoggerFactory loggerFactory) {
        super();
        this.logger = loggerFactory.getLogger(V1M2EntryCategories.class);
    }

    @Override
    public MigrationVersion getVersion() {
        return MigrationVersion.fromVersion("1.2");
    }

    @Override
    public String getDescription() {
        return "Initializes entry categories.";
    }

    @Override
    public Integer getChecksum() {
        return 0x6e5cf68c;
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
        logger.info("Starting migration: \"01.02 Entry categories\".");
        try (final var jdbcStatements = new V1M2EntryCategoriesJdbcStetements(context.getConnection(),
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
            logger.info("Migration: \"01.02 Entry categories\" is successfully completed.");
        } catch (final SQLException e) {
            throw new MigrationException("Couldn't close opend exceptions.");
        } catch (final Exception e) {
            logger.error("Migration: \"01.02 Entry categories\" is failed.",
                    e);
            throw e;
        }
    }

    private void addCategories(final CategoryGroupData categoryGroupData, final JsonArray categories,
            final V1M2EntryCategoriesJdbcStetements jdbcStatements) {
        if (categories == null) {
            return;
        }
        logger.debug(l -> l.debug("Adding category group under category with id: {}.",
                categoryGroupData.getParentId()));
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
        logger.debug(l -> l.debug("Category group under category with id: {} is successfully added.",
                categoryGroupData.getParentId()));
    }

    private int insertCategory(final CategoryData categoryData, final PreparedStatement stmt)
            throws MigrationException {
        logger.debug(l -> l.debug("Inserting category: \"{}\".",
                categoryData.getName()));
        try (final ResultSet generatedKeys = callInsertAccount(stmt,
                categoryData)) {
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
            final String message = "Unable to get ID from inserted category";
            logger.error(message);
            throw new MigrationException(message);
        } catch (final SQLException e) {
            logger.error("Cannot insert category \"{}\" under group with id: {}.",
                    categoryData.getName(),
                    categoryData.getParentId(),
                    e);
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

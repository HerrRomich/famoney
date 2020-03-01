package com.hrrm.famoney.infrastructure.persistence.migration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.hrrm.famoney.function.throwing.ThrowingBiConsumer;
import com.hrrm.famoney.function.throwing.ThrowingFunction;

public abstract class JdbcMigrationStetemnets implements AutoCloseable {

    private final Map<String, PreparedStatement> namedPreparedStatements = new HashMap<>();
    private final Connection connection;
    private final String basePath;
    private final ClassLoader classLoader;

    public JdbcMigrationStetemnets(final Connection connection, final String basePath, final ClassLoader classLoader) {
        super();
        this.connection = connection;
        this.basePath = basePath;
        this.classLoader = classLoader;
    }

    private PreparedStatement prepareStatement(final String path, final int autoGeneratedKeys)
            throws MigrationException {
        try {
            return connection.prepareStatement(IOUtils.resourceToString(path,
                    StandardCharsets.UTF_8,
                    classLoader),
                    autoGeneratedKeys);
        } catch (final SQLException ex) {
            throw new MigrationException(MessageFormat.format("Cannot prepare statement from resource : {0}.",
                    path),
                ex);
        } catch (final IOException ex) {
            throw new MigrationException(MessageFormat.format(
                    "Cannot load resource with SQL statement: {0} from classpath.",
                    path),
                ex);
        }
    }

    protected PreparedStatement getStatement(final String path) throws MigrationException {
        return getStatement(basePath + path,
                Statement.NO_GENERATED_KEYS);
    }

    protected PreparedStatement getStatementWithGeneratedKeys(final String path) throws MigrationException {
        return getStatement(basePath + path,
                Statement.RETURN_GENERATED_KEYS);
    }

    protected PreparedStatement getStatement(final String path, final int generatedKeys) throws MigrationException {
        return namedPreparedStatements.computeIfAbsent(path,
                ThrowingFunction.sneaky(p -> prepareStatement(p,
                        generatedKeys)));
    }

    @Override
    public void close() throws SQLException {
        namedPreparedStatements.forEach(ThrowingBiConsumer.sneaky((path, statement) -> statement.close()));
    }

}

package com.hrrm.famoney.infrastructure.persistence.internal;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.transaction.control.ScopedWorkException;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jdbc.JDBCConnectionProvider;

import com.hrrm.famoney.infrastructure.persistence.DatabaseTimestampProvider;

@Component(service = DatabaseTimestampProvider.class)
public class DatabaseTimestampProviderImpl implements DatabaseTimestampProvider {

    private final TransactionControl txControl;
    private final Connection connection;

    @Activate
    public DatabaseTimestampProviderImpl(@Reference TransactionControl txControl, 
            @Reference(target="(name=accounts)") JDBCConnectionProvider connectionProvider) {
        super();
        this.txControl = txControl;
        this.connection = connectionProvider.getResource(txControl);
    }

    @Override
    public LocalDateTime getTimestamp() throws SQLException {
        try {
            return txControl.required(this::callGetTimeStamp);
        } catch (ScopedWorkException e) {
            throw e.as(SQLException.class);
        }
    }

    private LocalDateTime callGetTimeStamp() throws SQLException {
        try (final var resultSet = connection.prepareStatement("select current_timestamp(6)").executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getTimestamp(1).toLocalDateTime();
            } else {
                return LocalDateTime.MIN;
            }
        }
    }

}

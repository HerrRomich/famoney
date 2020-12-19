package com.hrrm.famoney.infrastructure.jaxrs.internal;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import com.hrrm.famoney.infrastructure.jaxrs.OperationTimestampProvider;
import com.hrrm.famoney.infrastructure.persistence.DatabaseTimestampProvider;

@Component(service = OperationTimestampProvider.class)
public class OperationTimestampProviderImpl implements OperationTimestampProvider {

    private final ThreadLocal<LocalDateTime> operationTimestampHolder = new ThreadLocal<>();

    private final Logger logger;
    private final DatabaseTimestampProvider databaseTimestampProvider;

    @Activate
    public OperationTimestampProviderImpl(@Reference(
            service = LoggerFactory.class) final Logger logger,
            @Reference DatabaseTimestampProvider databaseTimestampProvider) {
        super();
        this.logger = logger;
        this.databaseTimestampProvider = databaseTimestampProvider;
    }

    @Override
    public void setTimestamp() {
        logger.debug("Setting operation timestamp to current database timestamp.");
        try {
            final var operationTimestamp = databaseTimestampProvider.getTimestamp();
            logger.trace("Setting operation timestamp to current database timestamp: {}.",
                operationTimestamp);
            operationTimestampHolder.set(operationTimestamp);
            logger.debug("Successfully set operation timestamp to current database timestamp.",
                operationTimestamp);
            logger.trace("Successfully set operation timestamp to current database timestamp: {}.",
                operationTimestamp);
        } catch (SQLException e) {
            final var serverTimestamp = LocalDateTime.now();
            logger.warn(
                "Couldn't read database timestamp. Setting operation timestamp to current server timestamp: {}.",
                serverTimestamp, e);
            operationTimestampHolder.set(serverTimestamp);
        }
    }

    @Override
    public LocalDateTime getTimestamp() {
        logger.debug("Getting current operation timestamp.");
        final var operationTimestamp = Optional.ofNullable(operationTimestampHolder.get())
            .orElseGet(() -> {
                final var serverTimestamp = LocalDateTime.now();
                logger.warn("Operation timestamp is not set. Getting current server timestamp: {}",
                    serverTimestamp);
                return serverTimestamp;
            });
        logger.debug("Got current operation timestamp.");
        logger.trace("Got current operation timestamp: {}.", operationTimestamp);
        return operationTimestamp;
    }

}

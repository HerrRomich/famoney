package com.hrrm.infrastructure.persistence;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import javax.persistence.AttributeConverter;

public abstract class LocalDateTimeAttributeBaseConverter implements
        AttributeConverter<LocalDateTime, Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(LocalDateTime value) {
        return Optional.ofNullable(value)
            .map(Timestamp::valueOf)
            .orElse(null);
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Timestamp value) {
        return Optional.ofNullable(value)
            .map(Timestamp::toLocalDateTime)
            .orElse(null);
    }

}

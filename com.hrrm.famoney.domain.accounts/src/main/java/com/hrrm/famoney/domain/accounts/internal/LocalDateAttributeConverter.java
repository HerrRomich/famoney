package com.hrrm.famoney.domain.accounts.internal;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class LocalDateAttributeConverter implements
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

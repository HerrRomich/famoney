package com.hrrm.famoney.domain.accounts.internal;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class LocalDateAttributeConverter implements AttributeConverter<LocalDate, Date> {

    @Override
    public Date convertToDatabaseColumn(LocalDate value) {
        return Optional.ofNullable(value)
            .map(Date::valueOf)
            .orElse(null);
    }

    @Override
    public LocalDate convertToEntityAttribute(Date value) {
        return Optional.ofNullable(value)
            .map(Date::toLocalDate)
            .orElse(null);
    }

}

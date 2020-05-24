package com.hrrm.famoney.api.accounts.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface MovementDataDTOBuilder<T extends MovementDataDTO> {

    public MovementDataDTOBuilder<T> date(LocalDate date);

    public MovementDataDTOBuilder<T> bookingDate(Optional<? extends LocalDate> bookingDate);

    public MovementDataDTOBuilder<T> budgetPeriod(Optional<? extends LocalDate> budgetPeriod);

    public MovementDataDTOBuilder<T> amount(BigDecimal amount);

    public MovementDataDTO build();

}

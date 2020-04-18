package com.hrrm.famoney.api.accounts.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface MovementDataDTOBuilder<T extends MovementDataDTO> {

    public MovementDataDTOBuilder<T> date(LocalDateTime date);

    public MovementDataDTOBuilder<T> bookingDate(LocalDateTime bookingDate);

    public MovementDataDTOBuilder<T> budgetMonth(LocalDate budgetMonth);

    public MovementDataDTOBuilder<T> amount(BigDecimal amount);

    public MovementDataDTO build();

}

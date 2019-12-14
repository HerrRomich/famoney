package com.hrrm.famoney.api.accounts.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "MovementSliceData", subTypes = { MovementSliceDTO.class })
public interface MovementSliceDataDTO {

    @Schema(required = true)
    LocalDate getDate();

    @Schema(required = true)
    Integer getMovementCount();

    @Schema(required = true)
    BigDecimal getMovementSum();

    @Schema(required = true)
    Integer getBookingCount();

    @Schema(required = true)
    BigDecimal getBookingSum();

}

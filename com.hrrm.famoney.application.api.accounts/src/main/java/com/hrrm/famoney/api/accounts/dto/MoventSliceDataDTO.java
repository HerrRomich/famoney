package com.hrrm.famoney.api.accounts.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "MovementSliceData", subTypes = { MovementSliceDTO.class })
public interface MoventSliceDataDTO {

    LocalDateTime getDate();

    Integer getCount();

    BigDecimal getSum();

}

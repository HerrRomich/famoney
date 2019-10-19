package com.hrrm.famoney.api.accounts.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "MovementData", subTypes = { MovementDTO.class })
public interface MovementDataDTO {

    public LocalDateTime getDate();

    public BigDecimal getAmount();

}

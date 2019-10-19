package com.hrrm.famoney.api.accounts.dto.internal;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.hrrm.famoney.api.accounts.dto.MovementDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Schema(name = "AccountMovement")
@Builder
@Getter
@ToString
public class MovementDTOImpl implements MovementDTO {

    private final Integer id;
    private final LocalDateTime date;
    private final BigDecimal amount;

}

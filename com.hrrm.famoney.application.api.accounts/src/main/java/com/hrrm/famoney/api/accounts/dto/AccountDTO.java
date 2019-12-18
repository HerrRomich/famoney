package com.hrrm.famoney.api.accounts.dto;

import java.math.BigDecimal;

import org.immutables.value.Value;

import com.hrrm.famoney.infrastructure.jaxrs.DTO;
import com.hrrm.famoney.infrastructure.jaxrs.ImmutableDtoStyle;
import com.hrrm.famoney.infrastructure.jaxrs.dto.IdDTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Account", allOf = { AccountDataDTO.class })
@Value.Immutable
@ImmutableDtoStyle
public interface AccountDTO extends DTO, IdDTO, AccountDataDTO {

    Integer getMovementCount();

    BigDecimal getSum();

}

package com.hrrm.famoney.api.accounts.dto;

import java.math.BigDecimal;

import org.immutables.value.Value;

import com.hrrm.famoney.infrastructure.jaxrs.ImmutableDtoStyle;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "EntryItemData")
@Value.Immutable
@ImmutableDtoStyle
public interface EntryItemDataDTO {

    @Schema(required = true)
    Integer getCategoryId();

    @Schema(required = true)
    BigDecimal getAmount();

    String getComments();

}

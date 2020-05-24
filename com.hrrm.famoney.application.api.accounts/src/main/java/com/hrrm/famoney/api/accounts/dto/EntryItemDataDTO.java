package com.hrrm.famoney.api.accounts.dto;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hrrm.famoney.api.accounts.dto.impl.EntryItemDataDTOImpl;
import com.hrrm.famoney.infrastructure.jaxrs.ImmutableDtoStyle;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "EntryItemData")
@JsonDeserialize(builder = EntryItemDataDTO.Builder.class)
@Value.Immutable
@ImmutableDtoStyle
public interface EntryItemDataDTO {

    public class Builder extends EntryItemDataDTOImpl.Builder {
    }

    @Schema(required = true)
    Integer getCategoryId();

    @Schema(required = true)
    BigDecimal getAmount();

    @Nullable
    String getComments();

}

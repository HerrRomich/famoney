package com.hrrm.famoney.api.accounts.dto;

import java.time.LocalDateTime;
import java.util.Set;

import org.immutables.value.Value;

import com.hrrm.famoney.infrastructure.jaxrs.ImmutableDtoStyle;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AccountData", subTypes = { AccountDTO.class })
@Value.Immutable
@ImmutableDtoStyle
public interface AccountDataDTO {

    @Schema(required = true)
    String getName();

    @Schema(required = true)
    LocalDateTime getOpenDate();

    Set<String> getTags();

}

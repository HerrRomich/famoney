package com.hrrm.famoney.api.accounts.sse;

import org.immutables.value.Value;

import com.hrrm.famoney.infrastructure.jaxrs.ImmutableDtoStyle;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ChangeAccountEvent")
@Value.Immutable
@ImmutableDtoStyle
public interface ChangeAccountEventDTO {

    Integer getAccountId();

}

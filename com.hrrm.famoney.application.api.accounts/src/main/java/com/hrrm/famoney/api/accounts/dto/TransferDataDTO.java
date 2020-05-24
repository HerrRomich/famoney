package com.hrrm.famoney.api.accounts.dto;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.hrrm.famoney.api.accounts.dto.impl.TransferDataDTOImpl;
import com.hrrm.famoney.infrastructure.jaxrs.ImmutableDtoStyle;

import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "TransferData", allOf = {
        MovementDataDTO.class
}, extensions = {
        @Extension(properties = {
                @ExtensionProperty(name = "x-discriminator-value", value = "refund")
        })
})
@JsonTypeName("transfer")
@Value.Immutable
@ImmutableDtoStyle
public interface TransferDataDTO extends MovementDataDTO {

    public class Builder extends TransferDataDTOImpl.Builder implements MovementDataDTOBuilder<TransferDataDTO> {

    }

    @Schema(required = true)
    Integer getOppositAccountId();

}

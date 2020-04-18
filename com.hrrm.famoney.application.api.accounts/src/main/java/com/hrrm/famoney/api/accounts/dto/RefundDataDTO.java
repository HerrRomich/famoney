package com.hrrm.famoney.api.accounts.dto;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.hrrm.famoney.api.accounts.dto.impl.RefundDataDTOImpl;
import com.hrrm.famoney.infrastructure.jaxrs.ImmutableDtoStyle;

import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "RefundData", allOf = {
        MovementDataDTO.class
}, extensions = {
        @Extension(properties = {
                @ExtensionProperty(name = "x-discriminator-value", value = "refund")
        })
})
@JsonTypeName("refund")
@Value.Immutable
@ImmutableDtoStyle
public interface RefundDataDTO extends MovementDataDTO, EntryItemDataDTO {

    public class Builder extends RefundDataDTOImpl.Builder implements MovementDataDTOBuilder<RefundDataDTO> {

    }

}

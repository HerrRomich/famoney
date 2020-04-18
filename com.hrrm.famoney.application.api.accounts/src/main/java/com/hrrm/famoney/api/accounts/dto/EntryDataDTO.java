package com.hrrm.famoney.api.accounts.dto;

import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.hrrm.famoney.api.accounts.dto.impl.EntryDataDTOImpl;
import com.hrrm.famoney.infrastructure.jaxrs.ImmutableDtoStyle;

import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "EntryData", allOf = {
        MovementDataDTO.class
}, extensions = {
        @Extension(properties = {
                @ExtensionProperty(name = "x-discriminator-value", value = "entry")
        })
})
@JsonTypeName("entry")
@Value.Immutable
@ImmutableDtoStyle
public interface EntryDataDTO extends MovementDataDTO {

    public class Builder extends EntryDataDTOImpl.Builder implements MovementDataDTOBuilder<EntryDataDTO> {

    }

    List<EntryItemDataDTO> getEntryItems();

}

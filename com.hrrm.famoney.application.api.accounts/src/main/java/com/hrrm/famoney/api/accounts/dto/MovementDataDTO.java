package com.hrrm.famoney.api.accounts.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.hrrm.famoney.infrastructure.jaxrs.DTO;

import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "MovementData", subTypes = {
        EntryDataDTO.class,
        RefundDataDTO.class,
        TransferDataDTO.class
}, discriminatorProperty = "type", discriminatorMapping = {
        @DiscriminatorMapping(schema = EntryDataDTO.class, value = "entry"),
        @DiscriminatorMapping(schema = RefundDataDTO.class, value = "refund"),
        @DiscriminatorMapping(schema = TransferDataDTO.class, value = "transfer")
})
@JsonSubTypes({
        @Type(name = "entry", value = EntryDataDTO.class),
        @Type(name = "refund", value = RefundDataDTO.class),
        @Type(name = "transfer", value = TransferDataDTO.class)
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public interface MovementDataDTO extends DTO {

    @Schema(required = true)
    LocalDateTime getDate();

    LocalDateTime getBookingDate();

    LocalDate getBudgetMonth();

    @Schema(required = true)
    BigDecimal getAmount();

}

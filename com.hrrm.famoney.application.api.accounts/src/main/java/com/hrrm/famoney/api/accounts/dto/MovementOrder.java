package com.hrrm.famoney.api.accounts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Order of movements.", name = "MovementOrder")
public enum MovementOrder {

    @JsonProperty("movement")
    MOVEMENT_DATE, @JsonProperty("booking")
    BOOKING_DATE

}

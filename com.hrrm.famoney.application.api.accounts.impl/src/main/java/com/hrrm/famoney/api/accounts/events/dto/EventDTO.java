package com.hrrm.famoney.api.accounts.events.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

public interface EventDTO {

    @Schema(required = true)
    LocalDateTime getTimestamp();
    
}

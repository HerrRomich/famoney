package com.hrrm.famoney.api.accounts.dto;

import java.util.Set;

import com.hrrm.famoney.infrastructure.jaxrs.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AccountData", subTypes = {AccountDTO.class})
public interface AccountDataDTO extends DTO {

    @Schema(required = true)
    public String getName();

    public Set<String> getTags();

}

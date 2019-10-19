package com.hrrm.famoney.api.accounts.dto;

import com.hrrm.famoney.infrastructure.jaxrs.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Account", allOf = { AccountDataDTO.class })
public interface AccountDTO extends DTO, WithIdDTO, AccountDataDTO {

}

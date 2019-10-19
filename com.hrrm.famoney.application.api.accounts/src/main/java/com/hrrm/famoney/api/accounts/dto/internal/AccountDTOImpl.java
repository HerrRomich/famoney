package com.hrrm.famoney.api.accounts.dto.internal;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hrrm.famoney.api.accounts.dto.AccountDTO;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@ToString
@JsonDeserialize(builder = AccountDTOImpl.AccountDTOImplBuilderImpl.class)
public class AccountDTOImpl extends AccountDataDTOImpl implements AccountDTO {

    private final Integer id;

    @JsonPOJOBuilder(withPrefix = "")
    public static final class AccountDTOImplBuilderImpl extends
            AccountDTOImplBuilder<AccountDTOImpl, AccountDTOImplBuilderImpl> {
    }
}

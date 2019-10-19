package com.hrrm.famoney.api.accounts.dto.internal;

import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hrrm.famoney.api.accounts.dto.AccountDataDTO;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@ToString
@JsonDeserialize(builder = AccountDataDTOImpl.AccountDataDTOImplBuilderImpl.class)
public class AccountDataDTOImpl implements AccountDataDTO {

    private final String name;
    private final Set<String> tags;

    @JsonPOJOBuilder(withPrefix = "")
    public static final class AccountDataDTOImplBuilderImpl extends
            AccountDataDTOImplBuilder<AccountDataDTOImpl, AccountDataDTOImplBuilderImpl> {
    }
}

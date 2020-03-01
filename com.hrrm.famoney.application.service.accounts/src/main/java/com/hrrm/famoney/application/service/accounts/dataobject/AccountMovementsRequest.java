package com.hrrm.famoney.application.service.accounts.dataobject;

import java.util.Optional;

import org.immutables.value.Value;

@Value.Immutable
public interface AccountMovementsRequest {

    Integer getAccountId();

    Optional<Integer> getOffset();

    Optional<Integer> getLimit();

}

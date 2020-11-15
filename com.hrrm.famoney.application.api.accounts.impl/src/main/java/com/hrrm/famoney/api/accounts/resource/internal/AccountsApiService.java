package com.hrrm.famoney.api.accounts.resource.internal;

import javax.validation.constraints.NotNull;

import com.hrrm.famoney.api.accounts.resource.internalexceptions.AccountsApiError;
import com.hrrm.famoney.domain.accounts.Account;

public interface AccountsApiService {

    Account getAccountByIdOrThrowNotFound(@NotNull Integer accountId, AccountsApiError error);

}

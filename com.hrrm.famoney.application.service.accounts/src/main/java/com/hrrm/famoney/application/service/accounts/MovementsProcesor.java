package com.hrrm.famoney.application.service.accounts;

import com.hrrm.famoney.application.service.accounts.dataobject.AccountMovementsRequest;
import com.hrrm.famoney.application.service.accounts.dataobject.AccountMovementsResponse;

public interface MovementsProcesor {

    AccountMovementsResponse getMovementsSlice(AccountMovementsRequest request);

}

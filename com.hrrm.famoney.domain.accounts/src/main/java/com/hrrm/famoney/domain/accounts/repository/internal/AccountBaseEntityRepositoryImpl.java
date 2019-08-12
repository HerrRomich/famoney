package com.hrrm.famoney.domain.accounts.repository.internal;

import com.hrrm.famoney.domain.accounts.AccountBaseEntity;
import com.hrrm.famoney.domain.accounts.repository.AccountBaseEntityRepository;

public class AccountBaseEntityRepositoryImpl extends AccountBaseJpaRepositoryImpl<AccountBaseEntity> implements
        AccountBaseEntityRepository<AccountBaseEntity> {

    @Override
    protected Class<AccountBaseEntity> getEntityClass() {
        return AccountBaseEntity.class;
    }

}

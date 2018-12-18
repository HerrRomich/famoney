package com.hrrm.famoney.domain.accounts.repository.internal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import com.hrrm.famoney.domain.accounts.Account;
import com.hrrm.famoney.domain.accounts.repository.AccountRepository;

@Component(service = AccountRepository.class, scope = ServiceScope.SINGLETON)
public class AccountRepositoryImpl extends AccountBaseJpaRepositoryImpl<Account> implements AccountRepository {

    @Override
    protected Class<Account> getEntityClass() {
        return Account.class;
    }

}

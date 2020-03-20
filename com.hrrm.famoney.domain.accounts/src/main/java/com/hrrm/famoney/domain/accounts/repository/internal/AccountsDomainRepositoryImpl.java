package com.hrrm.famoney.domain.accounts.repository.internal;

import org.osgi.service.log.LoggerFactory;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;

import com.hrrm.famoney.domain.accounts.AccountsDomainEntity;
import com.hrrm.famoney.domain.accounts.repository.AccountsDomainRepository;
import com.hrrm.famoney.infrastructure.persistence.JpaRepositoryImpl;

public abstract class AccountsDomainRepositoryImpl<T extends AccountsDomainEntity> extends JpaRepositoryImpl<T, Integer>
        implements AccountsDomainRepository<T> {

    public AccountsDomainRepositoryImpl(LoggerFactory loggerFactory, TransactionControl txControl,
            JPAEntityManagerProvider entityManagerProvider) {
        super(loggerFactory,
            txControl,
            entityManagerProvider);
    }

}

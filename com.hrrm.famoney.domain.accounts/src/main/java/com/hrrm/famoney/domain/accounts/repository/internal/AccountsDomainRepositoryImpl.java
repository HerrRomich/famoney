package com.hrrm.famoney.domain.accounts.repository.internal;

import javax.persistence.EntityManager;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;

import com.hrrm.famoney.domain.accounts.AccountsDomainEntity;
import com.hrrm.famoney.domain.accounts.repository.AccountsDomainRepository;
import com.hrrm.famoney.infrastructure.persistence.JpaRepositoryImpl;

public abstract class AccountsDomainRepositoryImpl<T extends AccountsDomainEntity> extends
        JpaRepositoryImpl<T, Integer> implements AccountsDomainRepository<T> {

    @Reference(target = "(name=accounts)")
    protected JPAEntityManagerProvider entityManagerProvider;

    private EntityManager entityManager;

    @Activate
    public void activate() {
        entityManager = entityManagerProvider.getResource(getTxControl());
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

}

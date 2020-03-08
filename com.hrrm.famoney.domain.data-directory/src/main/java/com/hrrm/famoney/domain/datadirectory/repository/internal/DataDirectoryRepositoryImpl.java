package com.hrrm.famoney.domain.datadirectory.repository.internal;

import javax.persistence.EntityManager;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;

import com.hrrm.famoney.domain.datadirectory.DataDirectoryDomainEntity;
import com.hrrm.famoney.domain.datadirectory.repository.DataDirectoryDomainRepository;
import com.hrrm.famoney.infrastructure.persistence.JpaRepositoryImpl;

public abstract class DataDirectoryRepositoryImpl<T extends DataDirectoryDomainEntity> extends
        JpaRepositoryImpl<T, Integer> implements DataDirectoryDomainRepository<T> {

    @Reference(target = "(name=data-directory)")
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

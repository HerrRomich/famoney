package com.hrrm.famoney.domain.datadirectory.repository.internal;

import org.osgi.service.log.LoggerFactory;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;

import com.hrrm.famoney.domain.datadirectory.DataDirectoryDomainEntity;
import com.hrrm.famoney.domain.datadirectory.repository.DataDirectoryDomainRepository;
import com.hrrm.famoney.infrastructure.persistence.JpaRepositoryImpl;

public abstract class DataDirectoryRepositoryImpl<T extends DataDirectoryDomainEntity> extends
        JpaRepositoryImpl<T, Integer> implements DataDirectoryDomainRepository<T> {

    public DataDirectoryRepositoryImpl(LoggerFactory loggerFactory, TransactionControl txControl,
            JPAEntityManagerProvider entityManagerProvider) {
        super(loggerFactory,
            txControl,
            entityManagerProvider);
    }

}

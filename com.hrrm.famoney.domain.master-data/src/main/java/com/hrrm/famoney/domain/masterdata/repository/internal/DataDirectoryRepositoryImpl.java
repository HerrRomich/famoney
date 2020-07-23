package com.hrrm.famoney.domain.masterdata.repository.internal;

import org.osgi.service.log.LoggerFactory;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;

import com.hrrm.famoney.domain.masterdata.MasterDataDomainEntity;
import com.hrrm.famoney.domain.masterdata.repository.MasterDataDomainRepository;
import com.hrrm.famoney.infrastructure.persistence.JpaRepositoryImpl;

public abstract class DataDirectoryRepositoryImpl<T extends MasterDataDomainEntity> extends
        JpaRepositoryImpl<T, Integer> implements MasterDataDomainRepository<T> {

    public DataDirectoryRepositoryImpl(LoggerFactory loggerFactory, TransactionControl txControl,
            JPAEntityManagerProvider entityManagerProvider) {
        super(loggerFactory,
            txControl,
            entityManagerProvider);
    }

}

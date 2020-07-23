package com.hrrm.famoney.domain.masterdata.repository;

import com.hrrm.famoney.domain.masterdata.MasterDataDomainEntity;
import com.hrrm.famoney.infrastructure.persistence.JpaRepository;

public interface MasterDataDomainRepository<T extends MasterDataDomainEntity> extends JpaRepository<T, Integer> {

}

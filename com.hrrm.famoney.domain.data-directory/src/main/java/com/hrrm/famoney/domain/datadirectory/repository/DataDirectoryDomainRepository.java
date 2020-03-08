package com.hrrm.famoney.domain.datadirectory.repository;

import com.hrrm.famoney.domain.datadirectory.DataDirectoryDomainEntity;
import com.hrrm.famoney.infrastructure.persistence.JpaRepository;

public interface DataDirectoryDomainRepository<T extends DataDirectoryDomainEntity> extends JpaRepository<T, Integer> {

}

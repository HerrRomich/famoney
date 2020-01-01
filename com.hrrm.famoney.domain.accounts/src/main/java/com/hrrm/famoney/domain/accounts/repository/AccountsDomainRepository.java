package com.hrrm.famoney.domain.accounts.repository;

import com.hrrm.famoney.domain.accounts.AccountsDomainEntity;
import com.hrrm.famoney.infrastructure.persistence.JpaRepository;

public interface AccountsDomainRepository<T extends AccountsDomainEntity> extends JpaRepository<T, Integer> {

}

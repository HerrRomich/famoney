package com.hrrm.famoney.domain.accounts.repository;

import com.hrrm.famoney.domain.accounts.AccountBaseEntity;
import com.hrrm.infrastructure.persistence.JpaRepository;

public interface AccountBaseEntityRepository<T extends AccountBaseEntity> extends JpaRepository<T, Integer> {

}

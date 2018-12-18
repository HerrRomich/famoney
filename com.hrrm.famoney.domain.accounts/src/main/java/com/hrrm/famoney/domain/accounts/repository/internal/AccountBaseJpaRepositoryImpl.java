package com.hrrm.famoney.domain.accounts.repository.internal;

import javax.persistence.EntityManager;

import org.osgi.service.component.annotations.Reference;

import com.hrrm.famoney.domain.accounts.AccountBaseEntity;
import com.hrrm.famoney.domain.accounts.repository.AccountBaseEntityRepository;
import com.hrrm.infrastructure.persistence.JpaRepositoryImpl;

public abstract class AccountBaseJpaRepositoryImpl<T extends AccountBaseEntity>
    extends JpaRepositoryImpl<T, Integer> implements AccountBaseEntityRepository<T> {

  @Reference(target = "(name=accounts)")
  protected EntityManager entityManager;

  @Override
  protected EntityManager getEntityManager() {
    return entityManager;
  }

}

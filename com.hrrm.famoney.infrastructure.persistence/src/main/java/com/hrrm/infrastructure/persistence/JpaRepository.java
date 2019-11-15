package com.hrrm.infrastructure.persistence;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface JpaRepository<T extends DomainEntity<P>, P extends Serializable> {

    List<T> findAll();

    Optional<T> find(P id);

    T save(T entity);

}

package com.hrrm.infrastructure.persistence;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.osgi.service.component.annotations.Reference;
import org.osgi.service.transaction.control.TransactionControl;

public abstract class JpaRepositoryImpl<T extends DomainEntity<P>, P extends Serializable> implements
        JpaRepository<T, P> {

    @Reference
    protected TransactionControl txControl;

    protected abstract Class<T> getEntityClass();

    protected abstract EntityManager getEntityManager();

    protected final TransactionControl getTxControl() {
        return txControl;
    }

    @Override
    public List<T> findAll() {
        return getTxControl().required(() -> getFindAllQuery().getResultList());
    }

    private TypedQuery<T> getFindAllQuery() {
        EntityManager entityManager = getEntityManager();
        CriteriaQuery<T> criteria = getEntityCriteriaQuery(entityManager);
        return entityManager.createQuery(criteria);
    }

    private CriteriaQuery<T> getEntityCriteriaQuery(EntityManager entityManager) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        return criteriaBuilder.createQuery(getEntityClass());
    }

    @Override
    public Optional<T> find(P id) {
        return getTxControl().supports(() -> {
            EntityManager entityManager = getEntityManager();
            return Optional.ofNullable(entityManager.find(getEntityClass(), id));
        });
    }

}

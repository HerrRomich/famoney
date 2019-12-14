package com.hrrm.infrastructure.persistence;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;
import org.osgi.service.transaction.control.ScopedWorkException;
import org.osgi.service.transaction.control.TransactionControl;

public abstract class JpaRepositoryImpl<T extends DomainEntity<P>, P extends Serializable>
        implements JpaRepository<T, P> {

    @Reference(service = LoggerFactory.class)
    protected Logger logger;

    @Reference
    protected TransactionControl txControl;

    protected abstract Class<T> getEntityClass();

    protected abstract EntityManager getEntityManager();

    protected final TransactionControl getTxControl() {
        return txControl;
    }

    @Override
    public List<T> findAll() {
        logger.debug("Searching all elements of entity class: {}.", getEntityClass());
        try {
            List<T> entities = getTxControl().required(() -> getFindAllQuery().getResultList());
            logger.debug(l -> l.debug("Successfully found {} elements of entity class: {}.",
                entities.size(), getEntityClass()));
            logger.trace(l -> l.trace("Successfully found {} elements of entity class: {}/p/n{}.",
                entities.size(), getEntityClass(), entities));
            return entities;
        } catch (RuntimeException ex) {
            logger.error("A problem by searching all elements of entity class: {}.",
                getEntityClass(), ex);
            throw ex;
        }
    }

    private TypedQuery<T> getFindAllQuery() {
        final var entityManager = getEntityManager();
        final var entityClass = getEntityClass();
        final var queryName = entityClass.getName()
            .concat("#findAll");
        return getNamedQueryOrAddNew(queryName, getEntityClass(), () -> {
            var criteria = getEntityCriteriaQuery(entityManager);
            return entityManager.createQuery(criteria);
        });
    }

    protected final <S> TypedQuery<S> getNamedQueryOrAddNew(String queryName, Class<S> resultClass,
        Supplier<TypedQuery<S>> querySupplier) {
        final var entityManager = getEntityManager();
        logger.debug("Trying to find a registered named query: {}", queryName);
        try {
            TypedQuery<S> namedQuery = txControl.supports(() -> entityManager.createNamedQuery(
                queryName, resultClass));
            logger.debug("A named query: {} is successfully found in registry.", queryName);
            return namedQuery;
        } catch (ScopedWorkException ex) {
            logger.debug(
                "A named query: {} was not found in registry. Trying to create and register a new one.",
                queryName, ex);
            var namedQuery = querySupplier.get();
            entityManager.getEntityManagerFactory()
                .addNamedQuery(queryName, namedQuery);
            logger.debug("A named query: {} is successfully created and put into registry.",
                queryName);
            return namedQuery;
        }
    }

    private CriteriaQuery<T> getEntityCriteriaQuery(EntityManager entityManager) {
        return entityManager.getCriteriaBuilder()
            .createQuery(getEntityClass());
    }

    @Override
    public Optional<T> find(P id) {
        final var entityClass = getEntityClass();
        logger.debug("Searching for an entity {} by its id: {}", entityClass, id);
        Optional<T> entity = getTxControl().supports(() -> Optional.ofNullable(getEntityManager()
            .find(entityClass, id)));
        entity.ifPresentOrElse(e -> {
            logger.debug("An entity {} by its id: {} is successfully found.", entityClass, id);
            logger.trace(l -> l.trace("An entity {} by its id: {} is successfully found./n/p{}",
                entityClass, id, e));
        }, () -> {
            logger.debug("An entity {} by its id: {} is successfully found.", entityClass, id);
        });
        return entity;
    }

    @Override
    public T save(T entity) {
        if (entity.getId() == null) {
            getEntityManager().persist(entity);
            return entity;
        } else if (!getEntityManager().contains(entity)) {
            return getEntityManager().merge(entity);
        } else {
            return entity;
        }
    }

}

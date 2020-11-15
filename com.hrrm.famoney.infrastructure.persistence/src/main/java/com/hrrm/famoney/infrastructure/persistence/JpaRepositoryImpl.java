package com.hrrm.famoney.infrastructure.persistence;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;

public abstract class JpaRepositoryImpl<T extends DomainEntity<P>, P extends Serializable> implements
        JpaRepository<T, P> {

    private Logger logger;

    private TransactionControl txControl;

    private EntityManager entityManager;

    protected final EntityManager getEntityManager() {
        return entityManager;
    }

    protected abstract Class<T> getEntityClass();

    protected final TransactionControl getTxControl() {
        return txControl;
    }

    public JpaRepositoryImpl(LoggerFactory loggerFactory, TransactionControl txControl,
            JPAEntityManagerProvider entityManagerProvider) {
        super();
        this.logger = loggerFactory.getLogger(JpaRepositoryImpl.class, Logger.class);
        this.txControl = txControl;
        entityManager = entityManagerProvider.getResource(getTxControl());
    }

    @Override
    public List<T> findAll() {
        logger.debug("Searching all elements of entity class: {}.", getEntityClass());
        try {
            final List<T> entities = getTxControl().required(() -> getFindAllQuery().getResultList());
            logger.debug("Successfully found {} elements of entity class: {}.", entities.size(), getEntityClass());
            logger.trace(l -> l.trace("Successfully found {} elements of entity class: {}/p/n{}.", entities.size(),
                    getEntityClass(), entities));
            return entities;
        } catch (final RuntimeException ex) {
            logger.error("A problem by searching all elements of entity class: {}.", getEntityClass(), ex);
            throw ex;
        }
    }

    private TypedQuery<T> getFindAllQuery() {
        final var entityClass = getEntityClass();
        final var queryName = entityClass.getName()
            .concat("#findAll");
        return getNamedQueryOrAddNew(queryName, getEntityClass(), () -> {
            final var criteria = getEntityCriteriaQuery(entityManager);
            return entityManager.createQuery(criteria);
        });
    }

    protected final <S> TypedQuery<S> getNamedQueryOrAddNew(final String queryName, final Class<S> resultClass,
            final Supplier<TypedQuery<S>> querySupplier) {
        return getNamedQueryOrAddQueryInternal(queryName, querySupplier, qn -> entityManager.createNamedQuery(qn,
                resultClass));
    }

    protected final Query getNamedQueryOrAddNew(final String queryName, final Supplier<Query> querySupplier) {
        return getNamedQueryOrAddQueryInternal(queryName, querySupplier, entityManager::createNamedQuery);
    }

    private <S extends Query> S getNamedQueryOrAddQueryInternal(final String queryName, final Supplier<S> querySupplier,
            final Function<String, S> queryCreator) {
        logger.debug("Trying to find a registered named query: {}", queryName);
        final var session = getSession();
        if (session.containsQuery(queryName)) {
            final var namedQuery = queryCreator.apply(queryName);
            logger.debug("A named query: {} is successfully found in registry.", queryName);
            return namedQuery;
        } else {
            logger.debug("A named query: {} was not found in registry. Trying to create and register a new one.",
                    queryName);
            final var namedQuery = querySupplier.get();
            entityManager.getEntityManagerFactory()
                .addNamedQuery(queryName, namedQuery);
            logger.debug("A named query: {} is successfully created and put into registry.", queryName);
            return namedQuery;
        }
    }

    private AbstractSession getSession() {
        return entityManager.unwrap(AbstractSession.class);
    }

    private CriteriaQuery<T> getEntityCriteriaQuery(final EntityManager entityManager) {
        return entityManager.getCriteriaBuilder()
            .createQuery(getEntityClass());
    }

    @Override
    public Optional<T> find(final P id) {
        final var entityClass = getEntityClass();
        logger.debug("Searching for an entity {} by its id: {}", entityClass, id);
        final Optional<T> entity = getTxControl().required(() -> Optional.ofNullable(getEntityManager().find(
                entityClass, id)));
        entity.ifPresentOrElse(e -> {
            logger.debug("An entity {} by its id: {} is successfully found.", entityClass, id);
            logger.trace(l -> l.trace("An entity {} by its id: {} is successfully found./n/p{}", entityClass, id, e));
        }, () -> logger.debug("An entity {} by its id: {} is successfully found.", entityClass, id));
        return entity;
    }

    @Override
    public T save(final T entity) {
        if (entity.getId() == null) {
            getEntityManager().persist(entity);
            return entity;
        } else if (getEntityManager().contains(entity)) {
            return entity;
        } else if (find(entity.getId()).isPresent()) {
            return getEntityManager().merge(entity);
        } else {
            getEntityManager().persist(entity);
            return entity;
        }
    }

    @Override
    public void lock(T entity, LockModeType lockModeType) {
        getEntityManager().lock(entity, lockModeType);
    }

    @Override
    public void flush() {
        getEntityManager().flush();
    }

    @Override
    public void delete(T entity) {
        entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
    }

}

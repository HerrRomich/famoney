package com.hrrm.famoney.domain.masterdata.repository.internal;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.metamodel.SingularAttribute;

import org.osgi.service.log.LoggerFactory;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;

import com.hrrm.famoney.domain.masterdata.EntryCategory;
import com.hrrm.famoney.domain.masterdata.repository.EntryCategoryRepository;

public abstract class EntryCategoryRepositoryImpl<T extends EntryCategory<T>> extends DataDirectoryRepositoryImpl<T>
        implements EntryCategoryRepository<T> {

    public EntryCategoryRepositoryImpl(LoggerFactory loggerFactory, TransactionControl txControl,
            JPAEntityManagerProvider entityManagerProvider) {
        super(loggerFactory,
            txControl,
            entityManagerProvider);
    }

    @Override
    public List<T> getTopLevelCategories() {
        return getTxControl().required(() -> {
            return getNamedQueryOrAddNew(getTopLevelCategoriesStatementName(),
                getEntityClass(),
                    this::createTopLevelCategoriesQuery).getResultList();
        });
    }

    protected abstract String getTopLevelCategoriesStatementName();

    private TypedQuery<T> createTopLevelCategoriesQuery() {
        final var cb = getEntityManager().getCriteriaBuilder();
        final var criteriaQuery = cb.createQuery(getEntityClass());
        final var root = criteriaQuery.from(getEntityClass());
        criteriaQuery.where(cb.isNull(root.get(getParentAttribute())));
        return getEntityManager().createQuery(criteriaQuery);
    }

    protected abstract SingularAttribute<T, T> getParentAttribute();

}

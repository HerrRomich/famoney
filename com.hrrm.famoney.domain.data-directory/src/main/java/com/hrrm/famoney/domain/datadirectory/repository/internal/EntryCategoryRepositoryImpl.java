package com.hrrm.famoney.domain.datadirectory.repository.internal;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.metamodel.SingularAttribute;

import com.hrrm.famoney.domain.datadirectory.EntryCategory;
import com.hrrm.famoney.domain.datadirectory.repository.EntryCategoryRepository;

public abstract class EntryCategoryRepositoryImpl<T extends EntryCategory<T>> extends DataDirectoryRepositoryImpl<T>
        implements EntryCategoryRepository<T> {

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

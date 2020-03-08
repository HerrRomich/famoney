package com.hrrm.famoney.domain.datadirectory.repository.internal;

import javax.persistence.metamodel.SingularAttribute;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import com.hrrm.famoney.domain.datadirectory.ExpenseCategory;
import com.hrrm.famoney.domain.datadirectory.ExpenseCategory_;
import com.hrrm.famoney.domain.datadirectory.repository.ExpenseCategoryRepository;

@Component(service = ExpenseCategoryRepository.class, scope = ServiceScope.SINGLETON)
public class ExpenseCategoryRepositoryImpl extends EntryCategoryRepositoryImpl<ExpenseCategory> implements
        ExpenseCategoryRepository {

    private static final String GET_TOP_LEVEL_CATEGORIES = ExpenseCategory.class.getName()
        .concat("#getTopLevelCategories");

    @Override
    protected Class<ExpenseCategory> getEntityClass() {
        return ExpenseCategory.class;
    }

    @Override
    protected String getTopLevelCategoriesStatementName() {
        return GET_TOP_LEVEL_CATEGORIES;
    }

    @Override
    protected SingularAttribute<ExpenseCategory, ExpenseCategory> getParentAttribute() {
        return ExpenseCategory_.parent;
    }

}

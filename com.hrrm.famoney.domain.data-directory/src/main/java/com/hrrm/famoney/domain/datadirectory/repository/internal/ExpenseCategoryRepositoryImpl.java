package com.hrrm.famoney.domain.datadirectory.repository.internal;

import javax.persistence.metamodel.SingularAttribute;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LoggerFactory;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;

import com.hrrm.famoney.domain.datadirectory.ExpenseCategory;
import com.hrrm.famoney.domain.datadirectory.ExpenseCategory_;
import com.hrrm.famoney.domain.datadirectory.repository.ExpenseCategoryRepository;

@Component(service = ExpenseCategoryRepository.class)
public class ExpenseCategoryRepositoryImpl extends EntryCategoryRepositoryImpl<ExpenseCategory> implements
        ExpenseCategoryRepository {

    private static final String GET_TOP_LEVEL_CATEGORIES = ExpenseCategory.class.getName()
        .concat("#getTopLevelCategories");

    @Activate
    public ExpenseCategoryRepositoryImpl(@Reference LoggerFactory loggerFactory,
            @Reference TransactionControl txControl, @Reference(
                    target = "(name=data-directory)") JPAEntityManagerProvider entityManagerProvider) {
        super(loggerFactory,
            txControl,
            entityManagerProvider);
    }

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

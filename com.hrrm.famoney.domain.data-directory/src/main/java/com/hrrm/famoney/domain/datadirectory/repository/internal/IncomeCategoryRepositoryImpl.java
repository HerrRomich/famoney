package com.hrrm.famoney.domain.datadirectory.repository.internal;

import javax.persistence.metamodel.SingularAttribute;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LoggerFactory;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;

import com.hrrm.famoney.domain.datadirectory.IncomeCategory;
import com.hrrm.famoney.domain.datadirectory.IncomeCategory_;
import com.hrrm.famoney.domain.datadirectory.repository.IncomeCategoryRepository;

@Component(service = IncomeCategoryRepository.class)
public class IncomeCategoryRepositoryImpl extends EntryCategoryRepositoryImpl<IncomeCategory> implements
        IncomeCategoryRepository {

    private static final String GET_TOP_LEVEL_CATEGORIES = IncomeCategory.class.getName()
        .concat("#getTopLevelCategories");

    @Activate
    public IncomeCategoryRepositoryImpl(@Reference LoggerFactory loggerFactory, @Reference TransactionControl txControl,
            @Reference(target = "(name=data-directory)") JPAEntityManagerProvider entityManagerProvider) {
        super(loggerFactory,
            txControl,
            entityManagerProvider);
    }

    @Override
    protected Class<IncomeCategory> getEntityClass() {
        return IncomeCategory.class;
    }

    @Override
    protected String getTopLevelCategoriesStatementName() {
        return GET_TOP_LEVEL_CATEGORIES;
    }

    @Override
    protected SingularAttribute<IncomeCategory, IncomeCategory> getParentAttribute() {
        return IncomeCategory_.parent;
    }

}

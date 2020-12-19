package com.hrrm.famoney.domain.accounts.repository.internal;

import java.util.List;

import javax.persistence.TypedQuery;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LoggerFactory;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;

import com.google.common.base.CaseFormat;
import com.hrrm.famoney.domain.accounts.Account;
import com.hrrm.famoney.domain.accounts.Account_;
import com.hrrm.famoney.domain.accounts.movement.Movement;
import com.hrrm.famoney.domain.accounts.repository.AccountRepository;

@Component(service = AccountRepository.class)
public class AccountRepositoryImpl extends AccountsDomainRepositoryImpl<Account> implements
        AccountRepository {

    private static enum QueryNames implements
            com.hrrm.famoney.infrastructure.persistence.QueryNames {
        FIND_ALL_ORDERED_BY_NAME,
        FIND_ALL_TAGS;

        private final String fullName;

        QueryNames() {
            fullName = Account.class.getName()
                    + "#"
                    + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.name());
        }

        @Override
        public String getFullName() {
            return fullName;
        }
    }

    @Activate
    public AccountRepositoryImpl(@Reference LoggerFactory loggerFactory,
            @Reference TransactionControl txControl, @Reference(
                    target = "(name=accounts)") JPAEntityManagerProvider entityManagerProvider) {
        super(loggerFactory, txControl, entityManagerProvider);
    }

    @Override
    protected Class<Account> getEntityClass() {
        return Account.class;
    }

    @Override
    public List<Account> findAllOrderedByName() {
        return getTxControl().required(() -> getNamedQueryOrAddNew(
            QueryNames.FIND_ALL_ORDERED_BY_NAME, Account.class,
            this::createFindAllOrderedByNameQuery).getResultList());
    }

    private TypedQuery<Account> createFindAllOrderedByNameQuery() {
        final var cb = getEntityManager().getCriteriaBuilder();
        final var criteriaQuery = cb.createQuery(Account.class);
        final var root = criteriaQuery.from(Account.class);
        criteriaQuery.select(root)
            .orderBy(cb.asc(root.get(Account_.name)));
        return getEntityManager().createQuery(criteriaQuery);
    }

    @Override
    public List<String> findAllTags() {
        return getTxControl().required(() -> getNamedQueryOrAddNew(QueryNames.FIND_ALL_TAGS,
            String.class, this::createFindAllTagsQuery).getResultList());
    }

    private TypedQuery<String> createFindAllTagsQuery() {
        final var cb = getEntityManager().getCriteriaBuilder();
        final var criteriaQuery = cb.createQuery(String.class);
        final var root = criteriaQuery.from(Account.class)
            .join(Account_.tags);
        criteriaQuery.select(root)
            .distinct(true);
        return getEntityManager().createQuery(criteriaQuery);
    }

}

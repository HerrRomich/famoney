package com.hrrm.famoney.domain.accounts.repository.internal;

import java.util.List;

import javax.persistence.TypedQuery;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import com.hrrm.famoney.domain.accounts.Account;
import com.hrrm.famoney.domain.accounts.Account_;
import com.hrrm.famoney.domain.accounts.repository.AccountRepository;

@Component(service = AccountRepository.class, scope = ServiceScope.SINGLETON)
public class AccountRepositoryImpl extends AccountsDomainRepositoryImpl<Account>
        implements AccountRepository {

    @Override
    protected Class<Account> getEntityClass() {
        return Account.class;
    }

    @Override
    public List<String> findAllTags() {
        return getTxControl().required(() -> getAllTagsQuery().getResultList());
    }

    private TypedQuery<String> getAllTagsQuery() {
        final var queryName = Account.class.getName()
            .concat("#findAllTags");
        return getNamedQueryOrAddNew(queryName, String.class,
                this::createAllTagsQuery);
    }

    private TypedQuery<String> createAllTagsQuery() {
        final var cb = getEntityManager().getCriteriaBuilder();
        final var criteriaQuery = cb.createQuery(String.class);
        final var root = criteriaQuery.from(Account.class)
            .join(Account_.tags);
        criteriaQuery.select(root)
            .distinct(true);
        return getEntityManager().createQuery(criteriaQuery);
    }

}

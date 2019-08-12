package com.hrrm.famoney.domain.accounts.repository.internal;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import com.hrrm.famoney.domain.accounts.Account;
import com.hrrm.famoney.domain.accounts.repository.AccountRepository;

@Component(service = AccountRepository.class, scope = ServiceScope.SINGLETON)
public class AccountRepositoryImpl extends AccountBaseJpaRepositoryImpl<Account> implements AccountRepository {

    @Override
    protected Class<Account> getEntityClass() {
        return Account.class;
    }

    @Override
    public List<String> findAllTags() {
        return getTxControl().required(() -> {
            var findAllTagsQuery = getEntityManager().createNamedQuery(Account.FIND_ALL_TAGS_QUERY, String.class);
            return findAllTagsQuery.getResultList();
        });
    }

}

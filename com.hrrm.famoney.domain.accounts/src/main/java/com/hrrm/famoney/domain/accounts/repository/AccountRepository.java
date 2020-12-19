package com.hrrm.famoney.domain.accounts.repository;

import java.util.List;

import com.hrrm.famoney.domain.accounts.Account;

public interface AccountRepository extends AccountsDomainRepository<Account> {

    List<String> findAllTags();

    List<Account> findAllOrderedByName();

}

package com.hrrm.famoney.application.jaxrs.accounts.internal;

import java.util.List;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;

import com.hrrm.famoney.application.jaxrs.accounts.AccountsApi;
import com.hrrm.famoney.application.jaxrs.accounts.dto.AccountDTO;
import com.hrrm.famoney.domain.accounts.repository.AccountRepository;

@Component(service = AccountsApi.class, scope = ServiceScope.PROTOTYPE)
@JaxrsResource
@JaxrsApplicationSelect("(osgi.jaxrs.name=com.hrrm.famoney.application.jaxrs.accounts)")
public class AccountsApiImpl implements AccountsApi {

    @Reference
    AccountRepository accountRepository;

    @Override
    public List<AccountDTO> getAllAccounts() {
        return accountRepository.findAll()
            .stream()
            .map(account -> new AccountDTO().id(account.getId())
                .name(account.getName()))
            .collect(Collectors.toList());
    }

}

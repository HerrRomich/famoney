package com.hrrm.famoney.api.accounts.resource.internal;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import com.hrrm.famoney.api.accounts.dto.AccountDTO;
import com.hrrm.famoney.api.accounts.dto.AccountMovementDTO;
import com.hrrm.famoney.api.accounts.dto.mapper.AccountEntityToDTOMapper;
import com.hrrm.famoney.api.accounts.resource.AccountsApi;
import com.hrrm.famoney.domain.accounts.repository.AccountRepository;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;

@Component(service = AccountsApi.class, scope = ServiceScope.BUNDLE)
@JaxrsResource
@JaxrsApplicationSelect("(osgi.jaxrs.name=com.hrrm.famoney.application.api.accounts)")
@Hidden
public class AccountsApiImpl implements AccountsApi {

    private Logger logger;
    private AccountRepository accountRepository;
    private AccountEntityToDTOMapper accountEntityToDTOMapper;

    @Activate
    public AccountsApiImpl(@Reference(service = LoggerFactory.class) Logger logger,
            @Reference AccountRepository accountRepository,
            @Reference AccountEntityToDTOMapper accountEntityToDTOMapper) {
        super();
        this.logger = logger;
        this.accountRepository = accountRepository;
        this.accountEntityToDTOMapper = accountEntityToDTOMapper;
    }

    @Override
    @Operation
    public List<AccountDTO> getAllAccounts() {
        logger.debug("Getting all accounts.");
        List<AccountDTO> result = accountRepository.findAll()
            .stream()
            .map(accountEntityToDTOMapper::toDTO)
            .collect(Collectors.toList());
        logger.debug("Got accounts.");
        logger.trace("Got {} accounts.", result.size());
        return result;
    }

    @Override
    public List<String> getAllAccountTags() {
        return accountRepository.findAllTags();
    }

    @Override
    public List<AccountMovementDTO> getAccountMovements(@NotNull Integer accountId, String continueToken, Integer count) {
        logger.debug("Getting all account movemnts.");
        logger.trace("Getting all movemnts of account with ID: {}", accountId);
        List<AccountMovementDTO> accountMovements = Collections.emptyList();
        logger.debug("Got all account movemnts.");
        logger.trace("Got {} movemnts of account with ID: {}", accountMovements.size(), accountId);
        return accountMovements;
    }

}

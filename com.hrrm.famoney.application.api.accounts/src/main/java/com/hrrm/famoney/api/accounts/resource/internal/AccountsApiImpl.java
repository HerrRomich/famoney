package com.hrrm.famoney.api.accounts.resource.internal;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.constraints.NotNull;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;
import org.osgi.service.transaction.control.TransactionControl;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;
import com.hrrm.famoney.api.accounts.dto.AccountDTO;
import com.hrrm.famoney.api.accounts.dto.AccountMovementDTO;
import com.hrrm.famoney.api.accounts.dto.mapper.AccountEntityToDTOMapper;
import com.hrrm.famoney.api.accounts.resource.AccountsApi;
import com.hrrm.famoney.domain.accounts.Account;
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
    private TransactionControl txControl;

    @Activate
    public AccountsApiImpl(@Reference(service = LoggerFactory.class) Logger logger,
            @Reference AccountRepository accountRepository,
            @Reference AccountEntityToDTOMapper accountEntityToDTOMapper, @Reference TransactionControl txControl) {
        super();
        this.logger = logger;
        this.accountRepository = accountRepository;
        this.accountEntityToDTOMapper = accountEntityToDTOMapper;
        this.txControl = txControl;
    }

    @Override
    @Operation
    public List<AccountDTO> getAllAccounts(Set<String> tags) {
        logger.debug("Getting all accounts.");
        Predicate<Account> tagFilterCondition = (tags == null || tags.isEmpty()) ? Predicates.alwaysTrue()
                : account -> !Sets.intersection(account.getTags(), tags)
                    .isEmpty();
        return txControl.requiresNew(() -> {
            Stream<Account> accountsStream = accountRepository.findAll()
                .stream()
                .filter(tagFilterCondition);

            List<AccountDTO> result = accountsStream.map(accountEntityToDTOMapper::toDTO)
                .collect(Collectors.toList());
            logger.debug("Got accounts.");
            logger.trace("Got {} accounts.", result.size());
            return result;
        });
    }

    @Override
    public List<String> getAllAccountTags() {
        return accountRepository.findAllTags();
    }

    @Override
    public List<AccountMovementDTO> getAccountMovements(@NotNull Integer accountId, String continueToken,
            Integer count) {
        logger.debug("Getting all account movemnts.");
        logger.trace("Getting all movemnts of account with ID: {}", accountId);
        List<AccountMovementDTO> accountMovements = Collections.emptyList();
        logger.debug("Got all account movemnts.");
        logger.trace("Got {} movemnts of account with ID: {}", accountMovements.size(), accountId);
        return accountMovements;
    }

}

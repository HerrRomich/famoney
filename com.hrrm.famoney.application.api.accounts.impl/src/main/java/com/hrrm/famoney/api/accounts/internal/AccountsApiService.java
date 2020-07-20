package com.hrrm.famoney.api.accounts.internal;

import java.text.MessageFormat;

import javax.validation.constraints.NotNull;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import com.hrrm.famoney.api.accounts.resource.internalexceptions.AccountsApiError;
import com.hrrm.famoney.domain.accounts.Account;
import com.hrrm.famoney.domain.accounts.repository.AccountRepository;
import com.hrrm.famoney.infrastructure.jaxrs.ApiException;

@Component(service = AccountsApiService.class)
public class AccountsApiService {

    private static final String NO_ACCOUNT_IS_FOUND_MESSAGE = "No account is found for id: {0}.";

    private final Logger logger;
    private final AccountRepository accountRepository;

    @Activate
    public AccountsApiService(@Reference(service = LoggerFactory.class) final Logger logger,
            @Reference final AccountRepository accountRepository) {
        super();
        this.logger = logger;
        this.accountRepository = accountRepository;
    }

    public Account getAccountByIdOrThrowNotFound(@NotNull final Integer accountId, final AccountsApiError error) {
        return accountRepository.find(accountId)
            .orElseThrow(() -> {
                final var errorMessage = MessageFormat.format(NO_ACCOUNT_IS_FOUND_MESSAGE,
                        accountId);
                final var exception = new ApiException(error,
                    errorMessage);
                logger.warn(errorMessage);
                logger.trace(errorMessage,
                        exception);
                return exception;
            });
    }

}

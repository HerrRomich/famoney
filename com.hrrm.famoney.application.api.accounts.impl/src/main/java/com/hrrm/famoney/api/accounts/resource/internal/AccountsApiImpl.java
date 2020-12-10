package com.hrrm.famoney.api.accounts.resource.internal;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;
import org.osgi.service.transaction.control.ScopedWorkException;
import org.osgi.service.transaction.control.TransactionControl;

import com.google.common.base.Predicates;
import com.google.common.collect.Sets;
import com.hrrm.famoney.api.accounts.dto.AccountDTO;
import com.hrrm.famoney.api.accounts.dto.AccountDataDTO;
import com.hrrm.famoney.api.accounts.dto.impl.AccountDTOImpl;
import com.hrrm.famoney.api.accounts.dto.impl.AccountDataDTOImpl;
import com.hrrm.famoney.api.accounts.events.AccountEventService;
import com.hrrm.famoney.api.accounts.resource.AccountsApi;
import com.hrrm.famoney.api.accounts.resource.internalexceptions.AccountsApiError;
import com.hrrm.famoney.domain.accounts.Account;
import com.hrrm.famoney.domain.accounts.repository.AccountRepository;
import com.hrrm.famoney.infrastructure.jaxrs.ApiException;

import io.swagger.v3.oas.annotations.Hidden;

@Component(service = {
        AccountsApi.class,
        AccountsApiService.class
})
@JaxrsResource
@JaxrsApplicationSelect("(osgi.jaxrs.name=com.hrrm.famoney.application.api.accounts)")
@Hidden
public class AccountsApiImpl implements AccountsApi, AccountsApiService {

    private static final String NO_ACCOUNT_IS_FOUND_MESSAGE = "No account is found for id: {0}.";

    private final Logger logger;
    private final AccountEventService accountEventService;
    private final AccountRepository accountRepository;
    private final TransactionControl txControl;

    @Context
    private HttpServletResponse httpServletResponse;
    @Context
    private UriInfo uriInfo;
    @Context
    private Sse sse;

    @Activate
    public AccountsApiImpl(@Reference(service = LoggerFactory.class) final Logger logger,
            @Reference final AccountEventService accountEventService,
            @Reference final AccountRepository accountRepository, @Reference final TransactionControl txControl) {
        super();
        this.logger = logger;
        this.accountEventService = accountEventService;
        this.accountRepository = accountRepository;
        this.txControl = txControl;
    }

    @Override
    public List<AccountDTO> getAllAccounts(final Set<String> tags) {
        logger.debug("Getting all accounts by {} tag(s).", tags.size());
        logger.trace(l -> l.trace("Getting all accounts by tag(s): {}.", tags));

        final var tagFilterCondition = Optional.ofNullable(tags)
            .filter(Predicate.not((Set::isEmpty)))
            .map(this::createTagPredicate)
            .orElse(Predicates.alwaysTrue());
        return txControl.requiresNew(() -> {
            final var accountsStream = accountRepository.findAll()
                .stream()
                .filter(tagFilterCondition);

            final var result = accountsStream.map(this::mapAccountToAccountDTO)
                .collect(Collectors.toList());
            logger.debug("Got {} accounts.", result.size());
            logger.trace(l -> l.trace("Got accounts: {}", result));
            return result;
        });
    }

    private Predicate<Account> createTagPredicate(final Set<String> t) {
        return account -> !Sets.intersection(account.getTags(), t)
            .isEmpty();
    }

    private AccountDTO mapAccountToAccountDTO(final Account account) {
        return AccountDTOImpl.builder()
            .id(account.getId())
            .from(mapAccountToAccountDataDTO(account))
            .movementCount(account.getMovementCount())
            .total(account.getMovementTotal())
            .build();
    }

    private AccountDataDTO mapAccountToAccountDataDTO(final Account account) {
        return AccountDataDTOImpl.builder()
            .name(account.getName())
            .openDate(account.getOpenDate())
            .tags(account.getTags())
            .build();
    }

    @Override
    public void addAccount(@NotNull final AccountDataDTO accountData) {
        logger.info("Creating new account.");
        logger.debug("Creating new account with name: {}.", accountData.getName());
        final var accountId = txControl.required(() -> {
            final var account = new Account().setName(accountData.getName())
                .setOpenDate(accountData.getOpenDate())
                .setTags(accountData.getTags());
            return accountRepository.save(account)
                .getId();
        });
        final var location = uriInfo.getAbsolutePathBuilder()
            .path(AccountsApi.class, "getAccount")
            .build(accountId);
        httpServletResponse.addHeader(HttpHeaders.LOCATION, location.toString());
        logger.info("New account is successfully created.");
        logger.debug("New account is successfully created with id: {}.", accountId);
    }

    @Override
    public AccountDataDTO changeAccount(@NotNull final Integer accountId, @NotNull final AccountDataDTO accountData) {
        try {
            return txControl.required(() -> {
                final var account = getAccountByIdOrThrowNotFound(accountId, AccountsApiError.NO_ACCOUNT_BY_CHANGE);
                account.setName(accountData.getName())
                    .setOpenDate(accountData.getOpenDate())
                    .setTags(accountData.getTags());
                accountRepository.save(account);
                return accountData;
            });
        } catch (final ScopedWorkException ex) {
            throw ex.as(ApiException.class);
        }
    }

    @Override
    public Account getAccountByIdOrThrowNotFound(@NotNull final Integer accountId, final AccountsApiError error) {
        return accountRepository.find(accountId)
            .orElseThrow(() -> {
                final var errorMessage = MessageFormat.format(NO_ACCOUNT_IS_FOUND_MESSAGE, accountId);
                final var exception = new ApiException(error,
                    errorMessage);
                logger.warn(errorMessage);
                logger.trace(errorMessage, exception);
                return exception;
            });
    }

    @Override
    public void deleteAccount(Integer accountId) {
        // TODO Auto-generated method stub
        //
        throw new UnsupportedOperationException();
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void sendChangeAccount(@Context SseEventSink sink) {
        final var eventBuilder = sse.newEventBuilder();
        accountEventService.registerEventListener()
            .map(changeAccountEvent -> eventBuilder
                    .mediaType(MediaType.APPLICATION_JSON_TYPE)
                    .data(changeAccountEvent)
                .build())
            .forEach(t -> {
                sink.send(t);
            })
            .onResolve(sink::close);
    }

    @Override
    public AccountDTO getAccount(@NotNull final Integer accountId) {
        logger.debug("Getting account info with ID: {}", accountId);
        final var account = getAccountByIdOrThrowNotFound(accountId, AccountsApiError.NO_ACCOUNT_ON_GET_ACCOUNT);
        logger.debug("Got account info with ID: {}", account.getId());
        return mapAccountToAccountDTO(account);
    }

}

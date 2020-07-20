package com.hrrm.famoney.domain.accounts.movement.repository.internal;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.metamodel.SingularAttribute;
import javax.validation.constraints.NotNull;

import org.eclipse.persistence.internal.jpa.querydef.CommonAbstractCriteriaImpl;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;

import com.hrrm.famoney.domain.accounts.AccountsDomainEntity_;
import com.hrrm.famoney.domain.accounts.movement.Movement;
import com.hrrm.famoney.domain.accounts.movement.Movement_;
import com.hrrm.famoney.domain.accounts.movement.repository.MovementRepository;
import com.hrrm.famoney.domain.accounts.repository.internal.AccountsDomainRepositoryImpl;

@Component(service = MovementRepository.class)
public class MovementRepositoryImpl extends AccountsDomainRepositoryImpl<Movement> implements MovementRepository {

    private static final String FIND_MOVEMENTS_BY_ACCOUNT_ID_BETWEEN_DATES = Movement.class.getName()
        .concat("#findMovementsByAccountIdBetweenDates");
    private static final String FIND_MOVEMENTS_BY_ACCOUNT_ID_BETWEEN_BOOKING_DATES = Movement.class.getName()
        .concat("#findMovementsByAccountIdBetweenBookingDates");

    private static final String FIND_MOVEMENTS_BY_ACCOUNT_ID_AFTER_MOVEMENT_DATE = Movement.class.getName()
        .concat("#findMovementsByAccountIdAfterMovementDate");
    private static final String FIND_MOVEMENTS_BY_ACCOUNT_ID_AFTER_BOOKING_DATE = Movement.class.getName()
        .concat("#findMovementsByAccountIdAfterBookingDate");
    private static final String FIND_NEXT_MOVEMENT_BY_ACCOUNT_ID_BEFORE_DATE = Movement.class.getName()
        .concat("#findNextMovementByAccountIdBeforeDate");
    private static final String ADJUST_MOVEMENTS_BY_ACCOUNT_ID_AFTER_DATE = Movement.class.getName()
        .concat("#adjustMovementsByAccountIdAfterDate");
    private static final String GET_MOVEMENTS_BY_ACCOUNT_ID_OFFSET_AND_LIMIT = Movement.class.getName()
        .concat("#getMovementsByAccountIdOffsetAndLimit");

    private static final String ACCOUNT_ID_PARAMETER_NAME = "accountId";
    private static final String DATE_FROM_PARAMETER_NAME = "dateFrom";
    private static final String DATE_TO_PARAMETER_NAME = "dateTo";
    private static final String AMOUNT_PARAMETER_NAME = "amount";
    private static final String MOVEMENT_ID_PARAMETER_NAME = "movementId";

    private Logger logger;

    @Activate
    public MovementRepositoryImpl(@Reference LoggerFactory loggerFactory, @Reference TransactionControl txControl,
            @Reference(target = "(name=accounts)") JPAEntityManagerProvider entityManagerProvider) {
        super(loggerFactory,
            txControl,
            entityManagerProvider);
        this.logger = loggerFactory.getLogger(MovementRepositoryImpl.class,
                Logger.class);
    }

    @Override
    protected Class<Movement> getEntityClass() {
        return Movement.class;
    }

    @Override
    public List<Movement> findByAccountIdBetweenDates(@NotNull final Integer accountId, final LocalDateTime dateFrom,
            final LocalDateTime dateTo) {
        logger.debug("Searching all movements by specified acount id: {} between dates {} and {}.",
                accountId,
                dateFrom,
                dateTo);
        final List<Movement> movements = getTxControl().required(() -> {
            final var query = getNamedQueryOrAddNew(FIND_MOVEMENTS_BY_ACCOUNT_ID_BETWEEN_DATES,
                    Movement.class,
                    this::createFindMovementsByAccountIdBetweenDatesQuery);
            return query.setParameter(ACCOUNT_ID_PARAMETER_NAME,
                    accountId)
                .setParameter(DATE_FROM_PARAMETER_NAME,
                        dateFrom)
                .setParameter(DATE_TO_PARAMETER_NAME,
                        dateTo)
                .getResultList();
        });
        logger.debug("Found {} movements by specified acount id: {} between dates {} and {}.",
                movements.size(),
                accountId,
                dateFrom,
                dateTo);
        logger.trace("Found {} movements by specified acount id: {} between dates {} and {}./p/n{}",
                movements.size(),
                accountId,
                dateFrom,
                dateTo,
                movements);
        return movements;
    }

    private TypedQuery<Movement> createFindMovementsByAccountIdBetweenDatesQuery() {
        final var cb = getEntityManager().getCriteriaBuilder();
        final var criteriaQuery = cb.createQuery(Movement.class);
        final var root = criteriaQuery.from(Movement.class);
        final var accountIdParameter = cb.parameter(Integer.class,
                ACCOUNT_ID_PARAMETER_NAME);
        final var dateFromParameter = cb.parameter(LocalDateTime.class,
                DATE_FROM_PARAMETER_NAME);
        final var dateToParameter = cb.parameter(LocalDateTime.class,
                DATE_TO_PARAMETER_NAME);
        criteriaQuery.where(cb.and(cb.equal(root.get(Movement_.account)
            .get(AccountsDomainEntity_.id),
                accountIdParameter),
                cb.greaterThanOrEqualTo(root.get(Movement_.date),
                        dateFromParameter),
                cb.lessThan(root.get(Movement_.date),
                        dateToParameter)))
            .orderBy(cb.asc(root.get(Movement_.date)));
        return getEntityManager().createQuery(criteriaQuery);
    }

    @Override
    public List<Movement> findByAccountIdBetweenBookingDates(@NotNull final Integer accountId,
            final LocalDateTime dateFrom, final LocalDateTime dateTo) {
        logger.debug("Searching all movements by specified acount id: {} between booking dates {} and {}.",
                accountId,
                dateFrom,
                dateTo);
        final List<Movement> movements = getTxControl().required(() -> {
            final var query = getNamedQueryOrAddNew(FIND_MOVEMENTS_BY_ACCOUNT_ID_BETWEEN_BOOKING_DATES,
                    Movement.class,
                    this::createFindMovementsByAccountIdBetweenBookingDatesQuery);
            return query.setParameter(ACCOUNT_ID_PARAMETER_NAME,
                    accountId)
                .setParameter(DATE_FROM_PARAMETER_NAME,
                        dateFrom)
                .setParameter(DATE_TO_PARAMETER_NAME,
                        dateTo)
                .getResultList();
        });
        logger.debug("Found {} movements by specified acount id: {} between booking dates {} and {}.",
                movements.size(),
                accountId,
                dateFrom,
                dateTo);
        logger.trace(l -> l.trace(
                "Found {} movements by specified acount id: {} between booking dates {} and {}./p/n{}",
                movements.size(),
                accountId,
                dateFrom,
                dateTo,
                movements));
        return movements;
    }

    private TypedQuery<Movement> createFindMovementsByAccountIdBetweenBookingDatesQuery() {
        final var cb = getEntityManager().getCriteriaBuilder();
        final var criteriaQuery = cb.createQuery(Movement.class);
        final var root = criteriaQuery.from(Movement.class);
        final var accountIdParameter = cb.parameter(Integer.class,
                ACCOUNT_ID_PARAMETER_NAME);
        final var dateFromParameter = cb.parameter(LocalDateTime.class,
                DATE_FROM_PARAMETER_NAME);
        final var dateToParameter = cb.parameter(LocalDateTime.class,
                DATE_TO_PARAMETER_NAME);
        criteriaQuery.where(cb.and(cb.equal(root.get(Movement_.account)
            .get(AccountsDomainEntity_.id),
                accountIdParameter),
                cb.greaterThanOrEqualTo(root.get(Movement_.bookingDate),
                        dateFromParameter),
                cb.lessThan(root.get(Movement_.bookingDate),
                        dateToParameter)))
            .orderBy(cb.asc(root.get(Movement_.bookingDate)));
        return getEntityManager().createQuery(criteriaQuery);
    }

    @Override
    public Long getMoventsCountByAccountId(@NotNull final Integer accountId) {
        logger.debug("Calculating count of movements by specified acount id: {}.",
                accountId);
        final long movementsCount = getTxControl().required(() -> getMoventsCountByAccountIdQuery().setParameter(
                ACCOUNT_ID_PARAMETER_NAME,
                accountId)
            .getSingleResult());
        logger.debug("Found {} movements by specified acount id: {}.",
                movementsCount,
                accountId);
        return movementsCount;
    }

    private TypedQuery<Long> getMoventsCountByAccountIdQuery() {
        final var queryName = Movement.class.getName()
            .concat("#getMoventsCountByAccountId");
        return getNamedQueryOrAddNew(queryName,
                Long.class,
                this::createMoventsCountByAccountIdQuery);
    }

    private TypedQuery<Long> createMoventsCountByAccountIdQuery() {
        final var cb = getEntityManager().getCriteriaBuilder();
        final var criteriaQuery = cb.createQuery(Long.class);
        final var root = criteriaQuery.from(Movement.class);
        final var accountIdParameter = cb.parameter(Integer.class,
                ACCOUNT_ID_PARAMETER_NAME);
        criteriaQuery.select(cb.count(root))
            .where(cb.equal(root.get(Movement_.account)
                .get(AccountsDomainEntity_.id),
                    accountIdParameter));
        return getEntityManager().createQuery(criteriaQuery);
    }

    @Override
    public List<Movement> findMovementsByAccountIdAfterDate(@NotNull final Integer accountId,
            final LocalDateTime dateFrom, final Optional<Integer> limitOptional) {
        return getTxControl().required(() -> {
            final var query = getNamedQueryOrAddNew(FIND_MOVEMENTS_BY_ACCOUNT_ID_AFTER_MOVEMENT_DATE,
                    Movement.class,
                    () -> createFindMovementsByAccountIdAfterDateQuery(Movement_.date));
            final var orderText = "movement date";
            return findMovementsByAccountIdAfterDate(accountId,
                    dateFrom,
                    limitOptional,
                    query,
                    orderText);
        });
    }

    @Override
    public List<Movement> findMovementsByAccountIdAfterBookingDate(@NotNull final Integer accountId,
            final LocalDateTime dateFrom, final Optional<Integer> limitOptional) {
        return getTxControl().required(() -> {
            final var query = getNamedQueryOrAddNew(FIND_MOVEMENTS_BY_ACCOUNT_ID_AFTER_BOOKING_DATE,
                    Movement.class,
                    () -> createFindMovementsByAccountIdAfterDateQuery(Movement_.bookingDate));
            final var orderText = "booking date";
            return findMovementsByAccountIdAfterDate(accountId,
                    dateFrom,
                    limitOptional,
                    query,
                    orderText);
        });
    }

    private TypedQuery<Movement> createFindMovementsByAccountIdAfterDateQuery(
            final SingularAttribute<Movement, LocalDateTime> dateAttribute) {
        final var cb = getEntityManager().getCriteriaBuilder();
        final var criteriaQuery = cb.createQuery(Movement.class);
        final var root = criteriaQuery.from(Movement.class);
        final var accountIdParameter = cb.parameter(Integer.class,
                ACCOUNT_ID_PARAMETER_NAME);
        final var dateFromParameter = cb.parameter(LocalDateTime.class,
                DATE_FROM_PARAMETER_NAME);
        criteriaQuery.where(cb.and(cb.equal(root.get(Movement_.account)
            .get(AccountsDomainEntity_.id),
                accountIdParameter),
                cb.greaterThanOrEqualTo(root.get(dateAttribute),
                        dateFromParameter)))
            .orderBy(cb.asc(root.get(dateAttribute)));
        return getEntityManager().createQuery(criteriaQuery);
    }

    private List<Movement> findMovementsByAccountIdAfterDate(final Integer accountId, final LocalDateTime dateFrom,
            final Optional<Integer> limitOptional, final TypedQuery<Movement> query, final String orderText) {
        logger.debug("Searching all movements by specified acount id: {} after {} {} with count {}.",
                accountId,
                orderText,
                dateFrom,
                limitOptional.map(Object::toString)
                    .orElse("ALL"));

        limitOptional.ifPresent(query::setMaxResults);
        final var movements = query.setParameter(ACCOUNT_ID_PARAMETER_NAME,
                accountId)
            .setParameter(DATE_FROM_PARAMETER_NAME,
                    dateFrom)
            .getResultList();
        logger.debug("Found {} movements by specified acount id: {} after {} " + "{} with count {}.",
                movements.size(),
                accountId,
                orderText,
                dateFrom,
                limitOptional.map(Object::toString)
                    .orElse("ALL"));
        logger.trace(l -> l.trace("Found {} movements by specified acount id: {} after {} " + "{} with count {}./p/n{}",
                movements.size(),
                accountId,
                orderText,
                dateFrom,
                movements));
        return movements;
    }

    @Override
    public Optional<Movement> findNextMovementByAccountIdBeforeDate(Integer accountId, Integer movementId) {
        logger.debug("Searching the next movement in a specified account before specified date.");
        logger.trace("Searching the next movement in an account with id: {} before date: {}.",
                accountId,
                movementId);
        return getTxControl().required(() -> {
            final var query = getNamedQueryOrAddNew(FIND_NEXT_MOVEMENT_BY_ACCOUNT_ID_BEFORE_DATE,
                    Movement.class,
                    this::createFindNextMovementByAccountIdBeforeDateQuery);
            query.setParameter(ACCOUNT_ID_PARAMETER_NAME,
                    accountId);
            query.setParameter(MOVEMENT_ID_PARAMETER_NAME,
                    movementId);
            query.setMaxResults(1);
            var movements = query.getResultList();
            if (movements.isEmpty()) {
                logger.debug("There is no movements in a specified account before specified date.");
                logger.trace("There is no movements in an account with id: {} before date: {}.",
                        accountId,
                        movementId);
                return Optional.empty();
            } else {
                final var movement = movements.get(0);
                logger.debug("The next movement is found in a specified account before specified date.");
                logger.trace(l -> l.trace("The next movement {} is found in an account with id: {} before date: {}.",
                        movement,
                        accountId,
                        movementId));
                return Optional.of(movement);
            }
        });
    }

    private TypedQuery<Movement> createFindNextMovementByAccountIdBeforeDateQuery() {
        final var cb = getEntityManager().getCriteriaBuilder();
        final var criteriaQuery = cb.createQuery(Movement.class);
        final var root = criteriaQuery.from(Movement.class);
        final var accountIdParameter = cb.parameter(Integer.class,
                ACCOUNT_ID_PARAMETER_NAME);
        final var movementIdParameter = cb.parameter(Integer.class,
                MOVEMENT_ID_PARAMETER_NAME);
        final var subQuery = criteriaQuery.subquery(LocalDateTime.class);
        final var subRoot = subQuery.from(Movement.class);
        subQuery.select(subRoot.get(Movement_.date))
            .where(cb.and(cb.equal(subRoot.get(Movement_.account)
                .get(AccountsDomainEntity_.id),
                    accountIdParameter),
                    cb.equal(subRoot.get(AccountsDomainEntity_.id),
                            movementIdParameter)));
        criteriaQuery.where(cb.and(cb.equal(root.get(Movement_.account)
            .get(AccountsDomainEntity_.id),
                accountIdParameter),
                cb.lessThan(root.get(Movement_.date),
                        subQuery.select(subRoot.get(Movement_.date)))))
            .orderBy(cb.desc(root.get(Movement_.date)));
        return getEntityManager().createQuery(criteriaQuery);
    }

    @Override
    public void adjustMovementSumsByAccountIdAfterDate(Integer accountId, LocalDateTime fromDate, BigDecimal amount) {
        logger.debug("Adjusting all movement sums in a specified account after specified date on a specified amount.");
        logger.debug("Adjusting all movement sums in an account with id: {} after date: {} on an amount: {}.",
                accountId,
                fromDate,
                amount);
        getTxControl().required(() -> {
            final var query = getNamedQueryOrAddNew(ADJUST_MOVEMENTS_BY_ACCOUNT_ID_AFTER_DATE,
                    this::createAdjustMovementsByAccountIdAfterDateQuery);
            query.setParameter(ACCOUNT_ID_PARAMETER_NAME,
                    accountId);
            query.setParameter(DATE_FROM_PARAMETER_NAME,
                    fromDate);
            query.setParameter(AMOUNT_PARAMETER_NAME,
                    amount);
            final var adjustedMovementsCount = query.executeUpdate();
            logger.debug(
                    "Adjusting sums of {} movment(s) in a specified account after specified date on a specified amount.",
                    adjustedMovementsCount);
            logger.debug("Adjusting sums of {} movment(s) in an account with id: {} after date: {} on an amount: {}.",
                    adjustedMovementsCount,
                    accountId,
                    fromDate,
                    amount);
            return null;
        });
    }

    private Query createAdjustMovementsByAccountIdAfterDateQuery() {
        final var cb = getEntityManager().getCriteriaBuilder();
        final var criteriaUpdate = cb.createCriteriaUpdate(Movement.class);
        final var root = criteriaUpdate.from(Movement.class);
        final var accountIdParameter = cb.parameter(Integer.class,
                ACCOUNT_ID_PARAMETER_NAME);
        final var dateFromParameter = cb.parameter(LocalDateTime.class,
                DATE_FROM_PARAMETER_NAME);
        final var amountParameter = cb.parameter(BigDecimal.class,
                AMOUNT_PARAMETER_NAME);
        ((CommonAbstractCriteriaImpl) criteriaUpdate).addParameter(amountParameter);
        criteriaUpdate.set(Movement_.total,
                cb.sum(root.get(Movement_.total),
                        amountParameter));
        criteriaUpdate.where(cb.and(cb.equal(root.get(Movement_.account)
            .get(AccountsDomainEntity_.id),
                accountIdParameter),
                cb.greaterThan(root.get(Movement_.date),
                        dateFromParameter)));
        return getEntityManager().createQuery(criteriaUpdate);
    }

    @Override
    public List<Movement> getMovementsByAccountIdOffsetAndLimit(@NotNull Integer accountId,
            Optional<Integer> offsetOptional, Optional<Integer> limitOptional) {
        logger.debug(
                "Getting specified count of movements in a specified account from specified position ordered by date.");
        logger.debug("Getting {} movement(s) in an account with id: {}, from position: {} ordered by date.",
                limitOptional.map(limit -> limit.toString())
                    .orElse("all"),
                accountId,
                offsetOptional.orElse(0));
        return getTxControl().required(() -> {
            final var query = getNamedQueryOrAddNew(GET_MOVEMENTS_BY_ACCOUNT_ID_OFFSET_AND_LIMIT,
                    Movement.class,
                    this::createGetMovementsByAccountId);
            query.setParameter(ACCOUNT_ID_PARAMETER_NAME,
                    accountId);
            offsetOptional.ifPresent(offset -> query.setFirstResult(offset));
            limitOptional.ifPresent(limit -> query.setMaxResults(limit));
            final var movements = query.getResultList();
            logger.debug("Found {} movment(s) in a specified account after specified date on a specified amount.",
                    movements.size());
            logger.debug("Found {} movement(s) in an account with id: {}, from position: {} ordered by date: {}",
                    movements.size(),
                    accountId,
                    limitOptional.map(limit -> limit.toString())
                        .orElse("all"),
                    accountId,
                    offsetOptional.orElse(0),
                    movements);
            return movements;
        });
    }

    private TypedQuery<Movement> createGetMovementsByAccountId() {
        final var cb = getEntityManager().getCriteriaBuilder();
        final var criteriaQuery = cb.createQuery(Movement.class);
        final var root = criteriaQuery.from(Movement.class);
        final var accountIdParameter = cb.parameter(Integer.class,
                ACCOUNT_ID_PARAMETER_NAME);
        criteriaQuery.where(cb.equal(root.get(Movement_.account)
            .get(AccountsDomainEntity_.id),
                accountIdParameter))
            .orderBy(cb.asc(root.get(Movement_.date)));
        return getEntityManager().createQuery(criteriaQuery);
    }

}

package com.hrrm.famoney.domain.accounts.movement.repository.internal;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.metamodel.SingularAttribute;

import org.eclipse.persistence.internal.jpa.querydef.CommonAbstractCriteriaImpl;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;

import com.hrrm.famoney.domain.accounts.Account;
import com.hrrm.famoney.domain.accounts.AccountsDomainEntity_;
import com.hrrm.famoney.domain.accounts.movement.Movement;
import com.hrrm.famoney.domain.accounts.movement.Movement_;
import com.hrrm.famoney.domain.accounts.movement.repository.MovementRepository;
import com.hrrm.famoney.domain.accounts.repository.internal.AccountsDomainRepositoryImpl;

@Component(service = MovementRepository.class)
public class MovementRepositoryImpl extends AccountsDomainRepositoryImpl<Movement> implements MovementRepository {

    private static final String GET_MOVEMENTS_COUNT_BY_ACCOUNT = Movement.class.getName()
        .concat("#getMoventsCountByAccount");
    private static final String FIND_MOVEMENTS_BY_ACCOUNT_BETWEEN_DATES = Movement.class.getName()
        .concat("#findMovementsByAccountBetweenDates");
    private static final String FIND_MOVEMENTS_BY_ACCOUNT_BETWEEN_BOOKING_DATES = Movement.class.getName()
        .concat("#findMovementsByAccountBetweenBookingDates");

    private static final String FIND_MOVEMENTS_BY_ACCOUNT_AFTER_MOVEMENT_DATE = Movement.class.getName()
        .concat("#findMovementsByAccountAfterMovementDate");
    private static final String FIND_MOVEMENTS_BY_ACCOUNT_AFTER_BOOKING_DATE = Movement.class.getName()
        .concat("#findMovementsByAccountAfterBookingDate");
    private static final String FIND_NEXT_MOVEMENT_BY_ACCOUNT_BEFORE_DATE = Movement.class.getName()
        .concat("#findNextMovementByAccountBeforeDate");
    private static final String FIND_NEXT_MOVEMENT_BY_ACCOUNT_BEFORE_BOOKING_DATE = Movement.class.getName()
        .concat("#findNextMovementByAccountBeforeBookingDate");
    private static final String ADJUST_MOVEMENTS_BY_ACCOUNT_AFTER_DATE = Movement.class.getName()
        .concat("#adjustMovementsByAccountAfterDate");
    private static final String GET_MOVEMENTS_BY_ACCOUNT_ORDERED_BY_DATE = Movement.class.getName()
        .concat("#getMovementsByAccountOffsetAndLimit");

    private static final String ACCOUNT_PARAMETER_NAME = "account";
    private static final String DATE_FROM_PARAMETER_NAME = "dateFrom";
    private static final String DATE_TO_PARAMETER_NAME = "dateTo";
    private static final String AMOUNT_PARAMETER_NAME = "amount";
    private static final String MOVEMENT_DATE_PARAMETER_NAME = "date";
    private static final String BOOKING_DATE_PARAMETER_NAME = "bookingDate";

    private Logger logger;

    @Activate
    public MovementRepositoryImpl(@Reference LoggerFactory loggerFactory, @Reference TransactionControl txControl,
            @Reference(target = "(name=accounts)") JPAEntityManagerProvider entityManagerProvider) {
        super(loggerFactory,
            txControl,
            entityManagerProvider);
        this.logger = loggerFactory.getLogger(MovementRepositoryImpl.class, Logger.class);
    }

    @Override
    protected Class<Movement> getEntityClass() {
        return Movement.class;
    }

    @Override
    public List<Movement> findByAccountBetweenDates(final Account account, final LocalDateTime dateFrom,
            final LocalDateTime dateTo) {
        logger.debug("Searching all movements by specified acount {} between dates {} and {}.", account, dateFrom,
                dateTo);
        final List<Movement> movements = getTxControl().required(() -> {
            final var query = getNamedQueryOrAddNew(FIND_MOVEMENTS_BY_ACCOUNT_BETWEEN_DATES, Movement.class,
                    this::createFindMovementsByAccountIdBetweenDatesQuery);
            return query.setParameter(ACCOUNT_PARAMETER_NAME, account)
                .setParameter(DATE_FROM_PARAMETER_NAME, dateFrom)
                .setParameter(DATE_TO_PARAMETER_NAME, dateTo)
                .getResultList();
        });
        logger.debug("Found {} movements by specified acount id: {} between dates {} and {}.", movements.size(),
                account, dateFrom, dateTo);
        logger.trace("Found {} movements by specified acount id: {} between dates {} and {}./p/n{}", movements.size(),
                account, dateFrom, dateTo, movements);
        return movements;
    }

    private TypedQuery<Movement> createFindMovementsByAccountIdBetweenDatesQuery() {
        final var cb = getEntityManager().getCriteriaBuilder();
        final var criteriaQuery = cb.createQuery(Movement.class);
        final var root = criteriaQuery.from(Movement.class);
        final var accountParameter = cb.parameter(Integer.class, ACCOUNT_PARAMETER_NAME);
        final var dateFromParameter = cb.parameter(LocalDateTime.class, DATE_FROM_PARAMETER_NAME);
        final var dateToParameter = cb.parameter(LocalDateTime.class, DATE_TO_PARAMETER_NAME);
        criteriaQuery.where(cb.and(cb.equal(root.get(Movement_.account), accountParameter), cb.greaterThanOrEqualTo(root
            .get(Movement_.date), dateFromParameter), cb.lessThan(root.get(Movement_.date), dateToParameter)))
            .orderBy(cb.asc(root.get(Movement_.date)));
        return getEntityManager().createQuery(criteriaQuery);
    }

    @Override
    public List<Movement> findByAccountBetweenBookingDates(final Account account, final LocalDateTime dateFrom,
            final LocalDateTime dateTo) {
        logger.debug("Searching all movements by specified acount {} between booking dates {} and {}.", account,
                dateFrom, dateTo);
        final List<Movement> movements = getTxControl().required(() -> {
            final var query = getNamedQueryOrAddNew(FIND_MOVEMENTS_BY_ACCOUNT_BETWEEN_BOOKING_DATES, Movement.class,
                    this::createFindMovementsByAccountIdBetweenBookingDatesQuery);
            return query.setParameter(ACCOUNT_PARAMETER_NAME, account)
                .setParameter(DATE_FROM_PARAMETER_NAME, dateFrom)
                .setParameter(DATE_TO_PARAMETER_NAME, dateTo)
                .getResultList();
        });
        logger.debug("Found {} movements by specified acount id: {} between booking dates {} and {}.", movements.size(),
                account, dateFrom, dateTo);
        logger.trace(l -> l.trace(
                "Found {} movements by specified acount id: {} between booking dates {} and {}./p/n{}", movements
                    .size(), account, dateFrom, dateTo, movements));
        return movements;
    }

    private TypedQuery<Movement> createFindMovementsByAccountIdBetweenBookingDatesQuery() {
        final var cb = getEntityManager().getCriteriaBuilder();
        final var criteriaQuery = cb.createQuery(Movement.class);
        final var root = criteriaQuery.from(Movement.class);
        final var accountParameter = cb.parameter(Account.class, ACCOUNT_PARAMETER_NAME);
        final var dateFromParameter = cb.parameter(LocalDateTime.class, DATE_FROM_PARAMETER_NAME);
        final var dateToParameter = cb.parameter(LocalDateTime.class, DATE_TO_PARAMETER_NAME);
        criteriaQuery.where(cb.and(cb.equal(root.get(Movement_.account), accountParameter), cb.greaterThanOrEqualTo(cb
            .coalesce(root.get(Movement_.bookingDate), root.get(Movement_.date)), dateFromParameter), cb.lessThan(cb
                .coalesce(root.get(Movement_.bookingDate), root.get(Movement_.date)), dateToParameter)))
            .orderBy(cb.asc(cb.coalesce(root.get(Movement_.bookingDate), root.get(Movement_.date))));
        return getEntityManager().createQuery(criteriaQuery);
    }

    @Override
    public Long getMoventsCountByAccount(final Account account) {
        logger.debug("Calculating count of movements by specified acount {}.", account);
        final long movementsCount = getTxControl().required(() -> getNamedQueryOrAddNew(GET_MOVEMENTS_COUNT_BY_ACCOUNT,
                Long.class, this::createMoventsCountByAccountQuery).setParameter(ACCOUNT_PARAMETER_NAME, account)
                    .getSingleResult());
        logger.debug("Found {} movements by specified acount id: {}.", movementsCount, account);
        return movementsCount;
    }

    private TypedQuery<Long> createMoventsCountByAccountQuery() {
        final var cb = getEntityManager().getCriteriaBuilder();
        final var criteriaQuery = cb.createQuery(Long.class);
        final var root = criteriaQuery.from(Movement.class);
        final var accountIdParameter = cb.parameter(Integer.class, ACCOUNT_PARAMETER_NAME);
        criteriaQuery.select(cb.count(root))
            .where(cb.equal(root.get(Movement_.account), accountIdParameter));
        return getEntityManager().createQuery(criteriaQuery);
    }

    @Override
    public List<Movement> findMovementsByAccountAfterDate(final Account account, final LocalDateTime dateFrom,
            final Integer limit) {
        return getTxControl().required(() -> {
            final var query = getNamedQueryOrAddNew(FIND_MOVEMENTS_BY_ACCOUNT_AFTER_MOVEMENT_DATE, Movement.class,
                    () -> createFindMovementsByAccountIdAfterDateQuery(Movement_.date));
            final var orderText = "movement date";
            return findMovementsByAccountAfterDate(account, dateFrom, limit, query, orderText);
        });
    }

    @Override
    public List<Movement> findMovementsByAccountAfterBookingDate(final Account account, final LocalDateTime dateFrom,
            final Integer limit) {
        return getTxControl().required(() -> {
            final var query = getNamedQueryOrAddNew(FIND_MOVEMENTS_BY_ACCOUNT_AFTER_BOOKING_DATE, Movement.class,
                    () -> createFindMovementsByAccountIdAfterDateQuery(Movement_.bookingDate));
            final var orderText = "booking date";
            return findMovementsByAccountAfterDate(account, dateFrom, limit, query, orderText);
        });
    }

    private TypedQuery<Movement> createFindMovementsByAccountIdAfterDateQuery(
            final SingularAttribute<Movement, LocalDateTime> dateAttribute) {
        final var cb = getEntityManager().getCriteriaBuilder();
        final var criteriaQuery = cb.createQuery(Movement.class);
        final var root = criteriaQuery.from(Movement.class);
        final var accountIdParameter = cb.parameter(Integer.class, ACCOUNT_PARAMETER_NAME);
        final var dateFromParameter = cb.parameter(LocalDateTime.class, DATE_FROM_PARAMETER_NAME);
        criteriaQuery.where(cb.and(cb.equal(root.get(Movement_.account)
            .get(AccountsDomainEntity_.id), accountIdParameter), cb.greaterThanOrEqualTo(root.get(dateAttribute),
                    dateFromParameter)))
            .orderBy(cb.asc(root.get(dateAttribute)));
        return getEntityManager().createQuery(criteriaQuery);
    }

    private List<Movement> findMovementsByAccountAfterDate(final Account account, final LocalDateTime dateFrom,
            final Integer limit, final TypedQuery<Movement> query, final String orderText) {
        final var limitOptional = Optional.ofNullable(limit);
        logger.debug("Searching all movements by specified acount id: {} after {} {} with count {}.", account,
                orderText, dateFrom, limitOptional.map(Object::toString)
                    .orElse("All"));

        limitOptional.ifPresent(query::setMaxResults);
        final var movements = query.setParameter(ACCOUNT_PARAMETER_NAME, account)
            .setParameter(DATE_FROM_PARAMETER_NAME, dateFrom)
            .getResultList();
        logger.debug("Found {} movements by specified acount id: {} after {} " + "{} with count {}.", movements.size(),
                account, orderText, dateFrom, limitOptional.map(Object::toString)
                    .orElse("ALL"));
        logger.trace(l -> l.trace("Found {} movements by specified acount id: {} after {} " + "{} with count {}./p/n{}",
                movements.size(), account, orderText, dateFrom, movements));
        return movements;
    }

    @Override
    public Optional<Movement> findNextMovementByAccountIdBeforeDate(final Account account, final LocalDateTime date) {
        logger.debug("Searching the next movement in a specified account before specified date.");
        logger.trace("Searching the next movement in an account with id: {} before date: {}.", account.getId(), date);
        return getTxControl().required(() -> {
            final var query = getNamedQueryOrAddNew(FIND_NEXT_MOVEMENT_BY_ACCOUNT_BEFORE_DATE, Movement.class,
                    this::createFindNextMovementByAccountBeforeDateQuery);
            query.setParameter(ACCOUNT_PARAMETER_NAME, account);
            query.setParameter(MOVEMENT_DATE_PARAMETER_NAME, date);
            query.setMaxResults(1);
            var movements = query.getResultList();
            if (movements.isEmpty()) {
                logger.debug("There is no movements in a specified account before specified date.");
                logger.trace("There is no movements in an account {} before date: {}.", account, date);
                return Optional.empty();
            } else {
                final var movement = movements.get(0);
                logger.debug("The next movement is found in a specified account before specified date.");
                logger.trace(l -> l.trace("The next movement {} is found in an account with id: {} before date: {}.",
                        movement, account, date));
                return Optional.of(movement);
            }
        });
    }

    private TypedQuery<Movement> createFindNextMovementByAccountBeforeDateQuery() {
        final var cb = getEntityManager().getCriteriaBuilder();
        final var criteriaQuery = cb.createQuery(Movement.class);
        final var root = criteriaQuery.from(Movement.class);
        final var accountParameter = cb.parameter(Account.class, ACCOUNT_PARAMETER_NAME);
        final var movementDateParameter = cb.parameter(LocalDateTime.class, MOVEMENT_DATE_PARAMETER_NAME);
        criteriaQuery.where(cb.and(cb.equal(root.get(Movement_.account), accountParameter), cb.lessThan(root.get(
                Movement_.date), movementDateParameter)))
            .orderBy(cb.desc(root.get(Movement_.date)));
        return getEntityManager().createQuery(criteriaQuery);
    }

    @Override
    public Optional<Movement> findNextMovementByAccountIdBeforeBookingDate(final Account account,
            final LocalDateTime date) {
        logger.debug("Searching the next movement in a specified account before specified booking date.");
        logger.trace("Searching the next movement in an account with id: {} before booking date: {}.", account.getId(),
                date);
        return getTxControl().required(() -> {
            final var query = getNamedQueryOrAddNew(FIND_NEXT_MOVEMENT_BY_ACCOUNT_BEFORE_BOOKING_DATE, Movement.class,
                    this::createFindNextMovementByAccountBeforeBookingDateQuery);
            query.setParameter(ACCOUNT_PARAMETER_NAME, account);
            query.setParameter(MOVEMENT_DATE_PARAMETER_NAME, date);
            query.setMaxResults(1);
            var movements = query.getResultList();
            if (movements.isEmpty()) {
                logger.debug("There is no movements in a specified account before specified booking date.");
                logger.trace("There is no movements in an account {} before booking date: {}.", account, date);
                return Optional.empty();
            } else {
                final var movement = movements.get(0);
                logger.debug("The next movement is found in a specified account before specified booking date.");
                logger.trace(l -> l.trace(
                        "The next movement {} is found in an account with id: {} before booking date: {}.", movement,
                        account, date));
                return Optional.of(movement);
            }
        });
    }

    private TypedQuery<Movement> createFindNextMovementByAccountBeforeBookingDateQuery() {
        final var cb = getEntityManager().getCriteriaBuilder();
        final var criteriaQuery = cb.createQuery(Movement.class);
        final var root = criteriaQuery.from(Movement.class);
        final var accountParameter = cb.parameter(Account.class, ACCOUNT_PARAMETER_NAME);
        final var bookingDateParameter = cb.parameter(LocalDateTime.class, BOOKING_DATE_PARAMETER_NAME);
        criteriaQuery.where(cb.and(cb.equal(root.get(Movement_.account), accountParameter), cb.lessThan(cb.coalesce(root
            .get(Movement_.bookingDate), root.get(Movement_.date)), bookingDateParameter)))
            .orderBy(cb.desc(cb.coalesce(root.get(Movement_.bookingDate), root.get(Movement_.date))));
        return getEntityManager().createQuery(criteriaQuery);
    }

    @Override
    public void adjustMovementSumsByAccountAfterDate(Account account, LocalDateTime fromDate, BigDecimal amount) {
        logger.debug("Adjusting all movement sums in a specified account after specified date on a specified amount.");
        logger.debug("Adjusting all movement sums in an account with id: {} after date: {} on an amount: {}.", account,
                fromDate, amount);
        getTxControl().required(() -> {
            final var query = getNamedQueryOrAddNew(ADJUST_MOVEMENTS_BY_ACCOUNT_AFTER_DATE,
                    this::createAdjustMovementsByAccountIdAfterDateQuery);
            query.setParameter(ACCOUNT_PARAMETER_NAME, account);
            query.setParameter(DATE_FROM_PARAMETER_NAME, fromDate);
            query.setParameter(AMOUNT_PARAMETER_NAME, amount);
            final var adjustedMovementsCount = query.executeUpdate();
            logger.debug(
                    "Adjusting sums of {} movment(s) in a specified account after specified date on a specified amount.",
                    adjustedMovementsCount);
            logger.debug("Adjusting sums of {} movment(s) in an account with id: {} after date: {} on an amount: {}.",
                    adjustedMovementsCount, account, fromDate, amount);
            return null;
        });
    }

    private Query createAdjustMovementsByAccountIdAfterDateQuery() {
        final var cb = getEntityManager().getCriteriaBuilder();
        final var criteriaUpdate = cb.createCriteriaUpdate(Movement.class);
        final var root = criteriaUpdate.from(Movement.class);
        final var accountParameter = cb.parameter(Account.class, ACCOUNT_PARAMETER_NAME);
        final var dateFromParameter = cb.parameter(LocalDateTime.class, DATE_FROM_PARAMETER_NAME);
        final var amountParameter = cb.parameter(BigDecimal.class, AMOUNT_PARAMETER_NAME);
        ((CommonAbstractCriteriaImpl<?>) criteriaUpdate).addParameter(amountParameter);
        criteriaUpdate.set(Movement_.total, cb.sum(root.get(Movement_.total), amountParameter));
        criteriaUpdate.where(cb.and(cb.equal(root.get(Movement_.account), accountParameter), cb.greaterThan(root.get(
                Movement_.date), dateFromParameter)));
        return getEntityManager().createQuery(criteriaUpdate);
    }

    @Override
    public List<Movement> getMovementsByAccountIdWithOffsetAndLimitOrderedByDate(final Account account,
            final Integer offset, final Integer limit) {
        final var offsetOptional = Optional.ofNullable(offset);
        final var limitOptional = Optional.ofNullable(limit);
        logger.debug(
                "Getting specified count of movements in a specified account from specified position ordered by date.");
        final var limitText = limitOptional.map(Object::toString)
            .orElse("all");
        final var offsetOrDefault = offsetOptional.orElse(0);
        logger.debug("Getting {} movement(s) in an account with id: {}, from position: {} ordered by date.", limitText,
                account.getId(), offsetOrDefault);
        return getTxControl().required(() -> {
            final var query = getNamedQueryOrAddNew(GET_MOVEMENTS_BY_ACCOUNT_ORDERED_BY_DATE, Movement.class,
                    this::createGetMovementsByAccountIdOrderedByDate);
            query.setParameter(ACCOUNT_PARAMETER_NAME, account);
            offsetOptional.ifPresent(query::setFirstResult);
            limitOptional.ifPresent(query::setMaxResults);
            final var movements = query.getResultList();
            logger.debug("Found {} movment(s) in a specified account after specified date on a specified amount.",
                    movements.size());
            logger.debug("Found {} movement(s) in an account with id: {}, from position: {} ordered by date: {}",
                    movements.size(), account, offsetOrDefault, movements);
            return movements;
        });
    }

    private TypedQuery<Movement> createGetMovementsByAccountIdOrderedByDate() {
        final var cb = getEntityManager().getCriteriaBuilder();
        final var criteriaQuery = cb.createQuery(Movement.class);
        final var root = criteriaQuery.from(Movement.class);
        final var accountParameter = cb.parameter(Account.class, ACCOUNT_PARAMETER_NAME);
        criteriaQuery.where(cb.equal(root.get(Movement_.account), accountParameter))
            .orderBy(cb.asc(root.get(Movement_.date)));
        return getEntityManager().createQuery(criteriaQuery);
    }

}

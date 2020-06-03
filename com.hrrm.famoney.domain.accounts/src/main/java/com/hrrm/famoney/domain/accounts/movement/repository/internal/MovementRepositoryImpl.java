package com.hrrm.famoney.domain.accounts.movement.repository.internal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.persistence.TypedQuery;
import javax.persistence.metamodel.SingularAttribute;
import javax.validation.constraints.NotNull;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;

import com.hrrm.famoney.domain.accounts.AccountsDomainEntity_;
import com.hrrm.famoney.domain.accounts.movement.Movement;
import com.hrrm.famoney.domain.accounts.movement.Movement_;
import com.hrrm.famoney.domain.accounts.movement.repository.MovementRepository;
import com.hrrm.famoney.domain.accounts.repository.internal.AccountsDomainRepositoryImpl;

@Component(service = MovementRepository.class, scope = ServiceScope.SINGLETON)
public class MovementRepositoryImpl extends AccountsDomainRepositoryImpl<Movement> implements MovementRepository {

    private static final String FIND_MOVEMENTS_BY_ACCOUNT_ID_BETWEEN_DATES = Movement.class.getName()
        .concat("#findMovementsByAccountIdBetweenDates");
    private static final String FIND_MOVEMENTS_BY_ACCOUNT_ID_BETWEEN_BOOKING_DATES = Movement.class.getName()
        .concat("#findMovementsByAccountIdBetweenBookingDates");

    private static final String FIND_MOVEMENTS_BY_ACCOUNT_ID_AFTER_MOVEMENT_DATE = Movement.class.getName()
        .concat("#findMovementsByAccountIdAfterMovementDate");
    private static final String FIND_MOVEMENTS_BY_ACCOUNT_ID_AFTER_BOOKING_DATE = Movement.class.getName()
        .concat("#findMovementsByAccountIdAfterBookingDate");

    private static final String ACCOUNT_ID_PARAMETER_NAME = "accountId";
    private static final String DATE_FROM_PARAMETER_NAME = "dateFrom";
    private static final String DATE_TO_PARAMETER_NAME = "dateTo";

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

}

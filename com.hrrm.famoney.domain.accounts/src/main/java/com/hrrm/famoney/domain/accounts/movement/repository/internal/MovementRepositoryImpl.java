package com.hrrm.famoney.domain.accounts.movement.repository.internal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.ParameterExpression;

import org.eclipse.persistence.internal.jpa.querydef.CommonAbstractCriteriaImpl;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;

import com.google.common.base.CaseFormat;
import com.hrrm.famoney.domain.accounts.Account;
import com.hrrm.famoney.domain.accounts.movement.Movement;
import com.hrrm.famoney.domain.accounts.movement.Movement_;
import com.hrrm.famoney.domain.accounts.movement.repository.MovementRepository;
import com.hrrm.famoney.domain.accounts.repository.internal.AccountsDomainRepositoryImpl;

@Component(service = MovementRepository.class)
public class MovementRepositoryImpl extends AccountsDomainRepositoryImpl<Movement> implements
        MovementRepository {

    private static enum QueryNames implements
            com.hrrm.famoney.infrastructure.persistence.QueryNames {
        GET_MOVEMENTS_COUNT_BY_ACCOUNT,
        FIND_MOVEMENTS_BY_ACCOUNT_AFTER_MOVEMENT_DATE,
        FIND_NEXT_MOVEMENT_BY_ACCOUNT_BEFORE_POSITION,
        ADJUST_MOVEMENTS_BACKWARD_BY_ACCOUNT_AFTER_POSITION,
        ADJUST_MOVEMENTS_FORWARD_BY_ACCOUNT_AFTER_POSITION,
        GET_MOVEMENTS_BY_ACCOUNT_OFFSET_AND_LIMIT_ORDERED_BY_POS,
        GET_LAST_POSITION_BY_ACCOUNT_ON_DATE;

        private final String fullName;

        QueryNames() {
            fullName = Movement.class.getName()
                    + "#"
                    + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.name());
        }

        @Override
        public String getFullName() {
            return fullName;
        }
    }

    private static enum ParameterNames implements
            com.hrrm.famoney.infrastructure.persistence.ParameterNames {
        ACCOUNT,
        ACCOUNT_ID,
        POSITION,
        AMOUNT,
        DATE,
        DATE_FROM;

        private final String fullName;

        ParameterNames() {
            fullName = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.name());

        }

        public String getFullName() {
            return fullName;
        }
    }

    private final Logger logger;

    private ParameterExpression<Account> accountParameter;
    private ParameterExpression<Integer> positionParameter;
    private ParameterExpression<BigDecimal> amountParameter;
    private ParameterExpression<LocalDate> dateParameter;

    @Activate
    public MovementRepositoryImpl(@Reference LoggerFactory loggerFactory,
            @Reference TransactionControl txControl, @Reference(
                    target = "(name=accounts)") JPAEntityManagerProvider entityManagerProvider) {
        super(loggerFactory, txControl, entityManagerProvider);
        this.logger = loggerFactory.getLogger(MovementRepositoryImpl.class, Logger.class);
    }

    @Override
    protected Class<Movement> getEntityClass() {
        return Movement.class;
    }

    @Override
    public Long getMovementsCountByAccount(final Account account) {
        logger.debug("Calculating count of movements by specified acount {}.", account);
        final long movementsCount = getTxControl().required(() -> getNamedQueryOrAddNew(
            QueryNames.GET_MOVEMENTS_COUNT_BY_ACCOUNT, Long.class,
            this::createMoventsCountByAccountQuery).setParameter(getAccountParameter(), account)
                .getSingleResult());
        logger.debug("Found {} movements by specified acount id: {}.", movementsCount, account);
        return movementsCount;
    }

    private ParameterExpression<Account> getAccountParameter() {
        if (accountParameter == null) {
            accountParameter = getEntityManager().getCriteriaBuilder()
                .parameter(Account.class, ParameterNames.ACCOUNT.getFullName());
        }
        return accountParameter;
    }

    private TypedQuery<Long> createMoventsCountByAccountQuery() {
        final var cb = getEntityManager().getCriteriaBuilder();
        final var criteriaQuery = cb.createQuery(Long.class);
        final var root = criteriaQuery.from(Movement.class);
        criteriaQuery.select(cb.count(root))
            .where(cb.equal(root.get(Movement_.account), getAccountParameter()));
        return getEntityManager().createQuery(criteriaQuery);
    }

    @Override
    public Optional<Movement> findNextMovementByAccountIdBeforePosition(final Account account,
        final Integer position) {
        logger.debug(
            "Searching the next movement in a specified account before specified position.");
        logger.trace("Searching the next movement in an account with id: {} before position: {}.",
            account.getId(), position);
        return getTxControl().required(() -> {
            final var query = getNamedQueryOrAddNew(
                QueryNames.FIND_NEXT_MOVEMENT_BY_ACCOUNT_BEFORE_POSITION, Movement.class,
                this::createFindNextMovementByAccountBeforePositionQuery);
            query.setParameter(getAccountParameter(), account);
            query.setParameter(getPositionParameter(), position);
            query.setMaxResults(1);
            var movements = query.getResultList();
            if (movements.isEmpty()) {
                logger.debug("There is no movements in a specified account before specified date.");
                logger.trace("There is no movements in an account {} before position: {}.", account,
                    position);
                return Optional.empty();
            } else {
                final var movement = movements.get(0);
                logger.debug(
                    "The next movement is found in a specified account before specified position.");
                logger.trace(l -> l.trace(
                    "The next movement {} is found in an account with id: {} before date: {}.",
                    movement, account, position));
                return Optional.of(movement);
            }
        });
    }

    private ParameterExpression<Integer> getPositionParameter() {
        if (positionParameter == null) {
            positionParameter = getEntityManager().getCriteriaBuilder()
                .parameter(Integer.class, ParameterNames.POSITION.getFullName());
        }
        return positionParameter;
    }

    private TypedQuery<Movement> createFindNextMovementByAccountBeforePositionQuery() {
        final var cb = getEntityManager().getCriteriaBuilder();
        final var criteriaQuery = cb.createQuery(Movement.class);
        final var root = criteriaQuery.from(Movement.class);
        criteriaQuery.where(cb.and(cb.equal(root.get(Movement_.account), getAccountParameter()), cb
            .lessThan(root.get(Movement_.position), getPositionParameter())))
            .orderBy(cb.desc(root.get(Movement_.position)));
        return getEntityManager().createQuery(criteriaQuery);
    }

    @Override
    public void rollbackMovementPositionsAndSumsByAccountAfterPosition(Movement movement,
        Integer position) {
        logger.debug(
            "Rolling back all movement sums on an amount: {} and positions in an account with id: {} after position: {} .",
            movement.getAmount(), movement.getAccount()
                .getId(), movement.getPosition());
        final var adjustedMovementsCount = getTxControl().required(() -> {
            final var query = getNamedQueryOrAddNew(
                QueryNames.ADJUST_MOVEMENTS_BACKWARD_BY_ACCOUNT_AFTER_POSITION,
                this::createAdjustBackwardsMovementsByAccountAfterPositionQuery);
            query.setParameter(getAccountParameter(), movement.getAccount());
            query.setParameter(getPositionParameter(), position);
            query.setParameter(getAmountParameter(), movement.getAmount()
                .negate());
            return query.executeUpdate();
        });
        logger.debug(
            "Rolling back of sums on an amount: {} and positions in {} movment(s) of an account with id: {} was successful.",
            adjustedMovementsCount, movement.getAmount(), movement.getPosition(), movement
                .getAccount()
                .getId());
    }

    private ParameterExpression<BigDecimal> getAmountParameter() {
        if (amountParameter == null) {
            amountParameter = getEntityManager().getCriteriaBuilder()
                .parameter(BigDecimal.class, ParameterNames.AMOUNT.getFullName());
        }
        return amountParameter;
    }

    @Override
    public void adjustMovementPositionsAndSumsByAccountAfterPosition(Movement movement,
        Integer position) {
        logger.debug(
            "Rolling back all movement sums on an amount: {} and positions in an account with id: {} after position: {} .",
            movement.getAmount(), movement.getAccount()
                .getId(), movement.getPosition());
        final var adjustedMovementsCount = getTxControl().required(() -> {
            final var query = getNamedQueryOrAddNew(
                QueryNames.ADJUST_MOVEMENTS_FORWARD_BY_ACCOUNT_AFTER_POSITION,
                this::createAdjustForwardsMovementsByAccountAfterPositionQuery);
            query.setParameter(getAmountParameter(), movement.getAmount());
            query.setParameter(getPositionParameter(), position);
            query.setParameter(getAccountParameter(), movement.getAccount());
            return query.executeUpdate();
        });
        logger.debug(
            "Rolling back of sums on an amount: {} and positions in {} movment(s) of an account with id: {} was successful.",
            adjustedMovementsCount, movement.getAmount(), movement.getPosition(), movement
                .getAccount()
                .getId());
    }

    private Query createAdjustForwardsMovementsByAccountAfterPositionQuery() {
        final var cb = getEntityManager().getCriteriaBuilder();
        final var criteriaUpdate = cb.createCriteriaUpdate(Movement.class);
        final var root = criteriaUpdate.from(Movement.class);
        ((CommonAbstractCriteriaImpl<?>) criteriaUpdate).addParameter(getAmountParameter());
        ((CommonAbstractCriteriaImpl<?>) criteriaUpdate).addParameter(getAccountParameter());
        ((CommonAbstractCriteriaImpl<?>) criteriaUpdate).addParameter(getPositionParameter());
        criteriaUpdate.set(Movement_.total, cb.sum(root.get(Movement_.total),
            getAmountParameter()));
        criteriaUpdate.set(Movement_.position, cb.sum(root.get(Movement_.position), 1));
        criteriaUpdate.where(cb.and(cb.equal(root.get(Movement_.account), getAccountParameter()), cb
            .greaterThanOrEqualTo(root.get(Movement_.position), getPositionParameter())));
        return getEntityManager().createQuery(criteriaUpdate);
    }

    private Query createAdjustBackwardsMovementsByAccountAfterPositionQuery() {
        final var cb = getEntityManager().getCriteriaBuilder();
        final var criteriaUpdate = cb.createCriteriaUpdate(Movement.class);
        final var root = criteriaUpdate.from(Movement.class);
        ((CommonAbstractCriteriaImpl<?>) criteriaUpdate).addParameter(getAmountParameter());
        ((CommonAbstractCriteriaImpl<?>) criteriaUpdate).addParameter(getAccountParameter());
        ((CommonAbstractCriteriaImpl<?>) criteriaUpdate).addParameter(getPositionParameter());
        criteriaUpdate.set(Movement_.total, cb.diff(root.get(Movement_.total),
            getAmountParameter()));
        criteriaUpdate.set(Movement_.position, cb.diff(root.get(Movement_.position), 1));
        criteriaUpdate.where(cb.and(cb.equal(root.get(Movement_.account), getAccountParameter()), cb
            .greaterThan(root.get(Movement_.position), getPositionParameter())));
        return getEntityManager().createQuery(criteriaUpdate);
    }

    @Override
    public List<Movement> getMovementsByAccountIdWithOffsetAndLimitOrderedByPos(
        final Account account, final Integer offset, final Integer limit) {
        final var offsetOptional = Optional.ofNullable(offset);
        final var limitOptional = Optional.ofNullable(limit);
        logger.debug(
            "Getting specified count of movements in a specified account from specified position ordered by date.");
        final var limitText = limitOptional.map(Object::toString)
            .orElse("all");
        final var offsetOrDefault = offsetOptional.orElse(0);
        logger.trace(
            "Getting {} movement(s) in an account with id: {}, from position: {} ordered by date.",
            limitText, account.getId(), offsetOrDefault);
        List<Movement> movements = getTxControl().required(() -> {
            final var query = getNamedQueryOrAddNew(
                QueryNames.GET_MOVEMENTS_BY_ACCOUNT_OFFSET_AND_LIMIT_ORDERED_BY_POS, Movement.class,
                this::createGetMovementsByAccountOffsetAndLimitIdOrderedByDate);
            query.setParameter(getAccountParameter(), account);
            query.setParameter(getPositionParameter(), offsetOptional.orElse(0));
            limitOptional.ifPresent(query::setMaxResults);
            return query.getResultList();
        });
        logger.debug(
            "Found {} movment(s) in a specified account after specified date on a specified amount.",
            movements.size());
        logger.debug(
            "Found {} movement(s) in an account with id: {}, from position: {} ordered by date: {}",
            movements.size(), account, offsetOrDefault, movements);
        return movements;
    }

    private TypedQuery<Movement> createGetMovementsByAccountOffsetAndLimitIdOrderedByDate() {
        final var cb = getEntityManager().getCriteriaBuilder();
        final var criteriaQuery = cb.createQuery(Movement.class);
        final var root = criteriaQuery.from(Movement.class);
        criteriaQuery.where(cb.and(cb.equal(root.get(Movement_.account), getAccountParameter()), cb
            .greaterThanOrEqualTo(root.get(Movement_.position), getPositionParameter())))
            .orderBy(cb.asc(root.get(Movement_.position)));
        return getEntityManager().createQuery(criteriaQuery);
    }

    @Override
    public Integer getLastPositionByAccountOnDate(Account account, LocalDate date) {
        logger.debug(
            "Calculating last possible position of movement on date {} in specified acount id: {}.",
            date, account.getId());
        final var position = getTxControl().required(() -> getNamedQueryOrAddNew(
            QueryNames.GET_LAST_POSITION_BY_ACCOUNT_ON_DATE, Long.class,
            this::createGetLastPositionByAccountOnDateQuery).setParameter(getAccountParameter(),
                account)
                .setParameter(getDateParameter(), date)
                .getSingleResult());
        logger.debug(
            "Last possible position {} in movements on date: {} by specified acount id: {}.",
            position, date, account.getId());
        return position.intValue();
    }

    private ParameterExpression<LocalDate> getDateParameter() {
        if (dateParameter == null) {
            dateParameter = getEntityManager().getCriteriaBuilder()
                .parameter(LocalDate.class, ParameterNames.DATE.getFullName());
        }
        return dateParameter;
    }

    private TypedQuery<Long> createGetLastPositionByAccountOnDateQuery() {
        final var cb = getEntityManager().getCriteriaBuilder();
        var criteriaQuery = cb.createQuery(Long.class);
        final var root = criteriaQuery.from(Movement.class);
        criteriaQuery = criteriaQuery.select(cb.count(root))
            .where(cb.and(cb.equal(root.get(Movement_.account), getAccountParameter()), cb
                .lessThanOrEqualTo(root.get(Movement_.date), getDateParameter())));
        return getEntityManager().createQuery(criteriaQuery);
    }

}

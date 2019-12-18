package com.hrrm.famoney.domain.accounts.movement.repository.internal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.Path;
import javax.persistence.metamodel.SingularAttribute;
import javax.validation.constraints.NotNull;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import com.hrrm.famoney.domain.accounts.Account;
import com.hrrm.famoney.domain.accounts.AccountsDomainEntity_;
import com.hrrm.famoney.domain.accounts.movement.MovementSlice;
import com.hrrm.famoney.domain.accounts.movement.MovementSlice_;
import com.hrrm.famoney.domain.accounts.movement.repository.MovementSliceRepository;
import com.hrrm.famoney.domain.accounts.repository.internal.AccountsDomainRepositoryImpl;

@Component(service = MovementSliceRepository.class, scope = ServiceScope.SINGLETON)
public class MovementSliceRepositoryImpl extends AccountsDomainRepositoryImpl<MovementSlice>
        implements MovementSliceRepository {

    private static final String GET_ALL_BY_ACCOUNT_ID_QUERY_NAME = MovementSlice.class.getName()
        .concat("#getAllByAccountId");
    private static final String FIND_FIRST_BY_ACCOUNT_ID_AFTER_DATE_QUERY_NAME = MovementSlice.class
        .getName()
        .concat("#findFirstByAccountIdAfterDate");
    private static final String FIND_LAST_BY_ACCOUNT_ID_BEFORE_OFFSET_BY_MOVEMENT_COUNT_QUERY_NAME = MovementSlice.class
        .getName()
        .concat("#find_LastByAccountIdBeforeOffsetByMovementCount");
    private static final String FIND_LAST_BY_ACCOUNT_ID_BEFORE_OFFSET_BY_BOOKING_COUNT_QUERY_NAME = MovementSlice.class
        .getName()
        .concat("#find_LastByAccountIdBeforeOffsetByBookingCount");

    private static final String ACCOUNT_ID_PARAMETER_NAME = "accountId";
    private static final String FROM_DATE_PARAMETER_NAME = "fromDate";
    private static final String COUNT_PARAMETER_NAME = "count";

    @Override
    public List<MovementSlice> getMovementSlicesByAccountId(@NotNull Integer accountId) {
        return getTxControl().required(() -> {
            var allByAccountIdQuery = getNamedQueryOrAddNew(GET_ALL_BY_ACCOUNT_ID_QUERY_NAME,
                    MovementSlice.class, this::createGetAllByAccountIdQuery);
            return allByAccountIdQuery.setParameter(ACCOUNT_ID_PARAMETER_NAME, accountId)
                .getResultList();
        });
    }

    private TypedQuery<MovementSlice> createGetAllByAccountIdQuery() {
        final var cb = getEntityManager().getCriteriaBuilder();
        final var criteriaQuery = cb.createQuery(MovementSlice.class);
        final var root = criteriaQuery.from(MovementSlice.class);
        final var accountIdParameter = cb.parameter(Integer.class, ACCOUNT_ID_PARAMETER_NAME);
        criteriaQuery.where(cb.equal(root.get(MovementSlice_.account)
            .get(AccountsDomainEntity_.id), accountIdParameter));
        return getEntityManager().createQuery(criteriaQuery);
    }

    @Override
    protected Class<MovementSlice> getEntityClass() {
        return MovementSlice.class;
    }

    @Override
    public Optional<MovementSlice> findFirstByAccountIdAfterDate(@NotNull Integer accountId,
            @NotNull LocalDateTime dateFrom) {
        return getTxControl().required(() -> {
            try {
                var query = getNamedQueryOrAddNew(FIND_FIRST_BY_ACCOUNT_ID_AFTER_DATE_QUERY_NAME,
                        MovementSlice.class, this::createFirstByAccountIdQuery);
                return Optional.of(query.setParameter(ACCOUNT_ID_PARAMETER_NAME, accountId)
                    .setParameter(FROM_DATE_PARAMETER_NAME, dateFrom)
                    .getSingleResult());
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    private TypedQuery<MovementSlice> createFirstByAccountIdQuery() {
        final var cb = getEntityManager().getCriteriaBuilder();
        final var criteriaQuery = cb.createQuery(MovementSlice.class);
        final var root = criteriaQuery.from(MovementSlice.class);
        final var accountIdParameter = cb.parameter(Integer.class, ACCOUNT_ID_PARAMETER_NAME);
        final var fromDateParameter = cb.parameter(LocalDateTime.class, FROM_DATE_PARAMETER_NAME);
        Path<Account> accountPath = root.get(MovementSlice_.account);
        Path<Integer> accountIdPath = accountPath.get(AccountsDomainEntity_.id);
        Path<LocalDateTime> accountSliceDatePath = root.get(MovementSlice_.date);
        criteriaQuery.where(cb.and(cb.equal(accountIdPath, accountIdParameter), cb.greaterThan(
                accountSliceDatePath, fromDateParameter)));
        return getEntityManager().createQuery(criteriaQuery)
            .setFirstResult(1);
    }

    @Override
    public Optional<MovementSlice> findLastByAccountBeforeOffsetByMovementDate(
            @NotNull Integer accountId, @NotNull Integer offset) {
        return getTxControl().required(() -> {
            try {
                var query = getNamedQueryOrAddNew(
                        FIND_LAST_BY_ACCOUNT_ID_BEFORE_OFFSET_BY_MOVEMENT_COUNT_QUERY_NAME,
                        MovementSlice.class,
                        this::createFindLastByAccountBeforeOffsetByMovementCountQuery);
                return Optional.of(query.setParameter(ACCOUNT_ID_PARAMETER_NAME, accountId)
                    .setParameter(COUNT_PARAMETER_NAME, offset)
                    .getSingleResult());
            } catch (NoResultException ex) {
                return Optional.empty();
            }
        });
    }

    private TypedQuery<MovementSlice> createFindLastByAccountBeforeOffsetByMovementCountQuery() {
        var countAttribute = MovementSlice_.movementCount;
        return createFindLastByAccountIdBeforeOffset(countAttribute);
    }

    @Override
    public Optional<MovementSlice> findLastByAccountBeforeOffsetByBookingDate(
            @NotNull Integer accountId, @NotNull Integer offset) {
        return getTxControl().required(() -> {
            try {
                var query = getNamedQueryOrAddNew(
                        FIND_LAST_BY_ACCOUNT_ID_BEFORE_OFFSET_BY_BOOKING_COUNT_QUERY_NAME,
                        MovementSlice.class,
                        this::createFindLastByAccountBeforeOffsetByBookingDateQuery);
                return Optional.of(query.setParameter(ACCOUNT_ID_PARAMETER_NAME, accountId)
                    .setParameter(COUNT_PARAMETER_NAME, offset)
                    .getSingleResult());
            } catch (NoResultException ex) {
                return Optional.empty();
            }
        });
    }

    private TypedQuery<MovementSlice> createFindLastByAccountBeforeOffsetByBookingDateQuery() {
        var countAttribute = MovementSlice_.bookingCount;
        return createFindLastByAccountIdBeforeOffset(countAttribute);
    }

    private TypedQuery<MovementSlice> createFindLastByAccountIdBeforeOffset(
            SingularAttribute<MovementSlice, Integer> countAttribute) {
        final var cb = getEntityManager().getCriteriaBuilder();
        final var criteriaQuery = cb.createQuery(MovementSlice.class);
        final var root = criteriaQuery.from(MovementSlice.class);
        final var accountIdParameter = cb.parameter(Integer.class, ACCOUNT_ID_PARAMETER_NAME);
        final var countParameter = cb.parameter(Integer.class, COUNT_PARAMETER_NAME);
        final var accountPath = root.get(MovementSlice_.account);
        final var accountIdPath = accountPath.get(AccountsDomainEntity_.id);
        final var accountSliceMovementCountPath = root.get(countAttribute);
        final var accountSliceDatePath = root.get(MovementSlice_.date);
        criteriaQuery.where(cb.and(cb.equal(accountIdPath, accountIdParameter), cb.greaterThan(
                accountSliceMovementCountPath, countParameter)))
            .orderBy(cb.asc(accountSliceDatePath));
        return getEntityManager().createQuery(criteriaQuery)
            .setFirstResult(1);
    }

}

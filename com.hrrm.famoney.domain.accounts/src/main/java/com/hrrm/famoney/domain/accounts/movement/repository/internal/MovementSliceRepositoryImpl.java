package com.hrrm.famoney.domain.accounts.movement.repository.internal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.Path;
import javax.validation.constraints.NotNull;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import com.hrrm.famoney.domain.accounts.Account;
import com.hrrm.famoney.domain.accounts.Account_;
import com.hrrm.famoney.domain.accounts.movement.MovementSlice;
import com.hrrm.famoney.domain.accounts.movement.MovementSlice_;
import com.hrrm.famoney.domain.accounts.movement.repository.MovementSliceRepository;
import com.hrrm.famoney.domain.accounts.repository.internal.AccountsDomainRepositoryImpl;

@Component(service = MovementSliceRepository.class, scope = ServiceScope.SINGLETON)
public class MovementSliceRepositoryImpl extends AccountsDomainRepositoryImpl<MovementSlice> implements
        MovementSliceRepository {

    private static final String ACCOUNT_ID_PARAMETER_NAME = "accountId";
    private static final String FROM_DATE_PARAMETER_NAME = "fromDate";

    @Override
    public List<MovementSlice> getMovementSlicesByAccountId(@NotNull Integer accountId) {
        return getTxControl().required(() -> getAllByAccountIdQuery().setParameter(ACCOUNT_ID_PARAMETER_NAME, accountId)
            .getResultList());
    }

    private TypedQuery<MovementSlice> getAllByAccountIdQuery() {
        final var queryName = MovementSlice.class.getName()
            .concat("#AllByAccountId");
        return getNamedQueryOrAddNew(queryName, MovementSlice.class, this::createAllByAccountIdQuery);
    }

    private TypedQuery<MovementSlice> createAllByAccountIdQuery() {
        final var cb = getEntityManager().getCriteriaBuilder();
        final var criteriaQuery = cb.createQuery(MovementSlice.class);
        final var root = criteriaQuery.from(MovementSlice.class);
        final var accountIdParameter = cb.parameter(Integer.class, ACCOUNT_ID_PARAMETER_NAME);
        criteriaQuery.where(cb.equal(root.get(MovementSlice_.account)
            .get(Account_.id), accountIdParameter));
        return getEntityManager().createQuery(criteriaQuery);
    }

    @Override
    protected Class<MovementSlice> getEntityClass() {
        return MovementSlice.class;
    }

    @Override
    public Optional<MovementSlice> findFirstByAccountIdAfterDate(@NotNull Integer accountId,
            @NotNull LocalDateTime dateFrom) {
        return getTxControl().required(() -> getFirstMovementSliceByAccountIdAfterDate(accountId, dateFrom));
    }

    private Optional<MovementSlice> getFirstMovementSliceByAccountIdAfterDate(@NotNull Integer accountId,
            @NotNull LocalDateTime dateFrom) {
        try {
            return Optional.of(getFirstByAccountIdAfterDateQuery().setParameter(ACCOUNT_ID_PARAMETER_NAME, accountId)
                .setParameter(FROM_DATE_PARAMETER_NAME, dateFrom)
                .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    private TypedQuery<MovementSlice> getFirstByAccountIdAfterDateQuery() {
        final var queryName = MovementSlice.class.getName()
            .concat("#FirstByAccountIdAfterDate");
        return getNamedQueryOrAddNew(queryName, MovementSlice.class, this::createFirstByAccountIdQuery);
    }

    private TypedQuery<MovementSlice> createFirstByAccountIdQuery() {
        final var cb = getEntityManager().getCriteriaBuilder();
        final var criteriaQuery = cb.createQuery(MovementSlice.class);
        final var root = criteriaQuery.from(MovementSlice.class);
        final var accountIdParameter = cb.parameter(Integer.class, ACCOUNT_ID_PARAMETER_NAME);
        final var fromDateParameter = cb.parameter(LocalDateTime.class, FROM_DATE_PARAMETER_NAME);
        Path<Account> accountPath = root.get(MovementSlice_.account);
        Path<Integer> accountIdPath = accountPath.get(Account_.id);
        Path<LocalDateTime> accountSliceDatePath = root.get(MovementSlice_.date);
        criteriaQuery.where(cb.and(cb.equal(accountIdPath, accountIdParameter), cb.greaterThan(accountSliceDatePath,
                fromDateParameter)));
        return getEntityManager().createQuery(criteriaQuery)
            .setFirstResult(1);
    }

}

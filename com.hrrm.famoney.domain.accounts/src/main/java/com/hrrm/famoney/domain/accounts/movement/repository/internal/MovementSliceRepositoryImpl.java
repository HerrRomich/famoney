package com.hrrm.famoney.domain.accounts.movement.repository.internal;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import com.hrrm.famoney.domain.accounts.Account_;
import com.hrrm.famoney.domain.accounts.movement.MovementSlice;
import com.hrrm.famoney.domain.accounts.movement.MovementSlice_;
import com.hrrm.famoney.domain.accounts.movement.repository.MovementSliceRepository;
import com.hrrm.famoney.domain.accounts.repository.internal.AccountsDomainRepositoryImpl;

@Component(service = MovementSliceRepository.class, scope = ServiceScope.SINGLETON)
public class MovementSliceRepositoryImpl extends
        AccountsDomainRepositoryImpl<MovementSlice> implements
        MovementSliceRepository {

    private static final String ACCOUNT_ID_PARAMETER_NAME = "accountId";

    @Override
    public List<MovementSlice> getMovementSlicesByAccountId(
            @NotNull Integer accountId) {
        return getTxControl().required(
                () -> getAllMovementSlicesByAccountIdQuery().setParameter(
                        ACCOUNT_ID_PARAMETER_NAME, accountId)
                    .getResultList());
    }

    private TypedQuery<MovementSlice> getAllMovementSlicesByAccountIdQuery() {
        final var queryName = MovementSlice.class.getName()
            .concat("#AllMovementSlicesByAccountId");
        return getNamedQueryOrAddNew(queryName, MovementSlice.class,
                this::createAllMovementSlicesByAccountIdQuery);
    }

    private TypedQuery<MovementSlice> createAllMovementSlicesByAccountIdQuery() {
        final var cb = getEntityManager().getCriteriaBuilder();
        final var criteriaQuery = cb.createQuery(MovementSlice.class);
        final var root = criteriaQuery.from(MovementSlice.class);
        final var accountIdParamaeter = cb.parameter(Integer.class,
                ACCOUNT_ID_PARAMETER_NAME);
        criteriaQuery.where(cb.equal(root.get(MovementSlice_.account)
            .get(Account_.id), accountIdParamaeter));
        return getEntityManager().createQuery(criteriaQuery);
    }

    @Override
    protected Class<MovementSlice> getEntityClass() {
        return MovementSlice.class;
    }

}

package com.hrrm.famoney.domain.accounts.movement.repository.internal;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import com.hrrm.famoney.domain.accounts.Account_;
import com.hrrm.famoney.domain.accounts.movement.Movement;
import com.hrrm.famoney.domain.accounts.movement.Movement_;
import com.hrrm.famoney.domain.accounts.movement.repository.MovementRepository;
import com.hrrm.famoney.domain.accounts.repository.internal.AccountsDomainRepositoryImpl;

@Component(service = MovementRepository.class, scope = ServiceScope.SINGLETON)
public class MovementRepositoryImpl extends AccountsDomainRepositoryImpl<Movement> implements MovementRepository {

    private static final String ACCOUNT_ID_PARAMETER_NAME = "accountId";

    @Override
    public List<Movement> findMovementsByAccountId(@NotNull Integer accountId) {
        logger.debug("Searching all movements by specified acount id: {}.", accountId);
        List<Movement> movements = getTxControl().required(() -> findAllMovementsByAccountIdQuery().setParameter(
                                                                                                                 ACCOUNT_ID_PARAMETER_NAME,
                                                                                                                 accountId)
            .getResultList());
        logger.debug("Found {} movements by specified acount id: {}.", movements.size(), accountId);
        logger.trace("Found {} movements by specified acount id: {}./p/n{}", movements.size(), accountId, movements);
        return movements;
    }

    private TypedQuery<Movement> findAllMovementsByAccountIdQuery() {
        final var queryName = Movement.class.getName()
            .concat("#findAllMovementsByAccountId");
        return getNamedQueryOrAddNew(queryName, Movement.class, this::createAllMovementsByAccountIdQuery);
    }

    private TypedQuery<Movement> createAllMovementsByAccountIdQuery() {
        final var cb = getEntityManager().getCriteriaBuilder();
        final var criteriaQuery = cb.createQuery(Movement.class);
        final var root = criteriaQuery.from(Movement.class);
        final var accountIdParamaeter = cb.parameter(Integer.class, ACCOUNT_ID_PARAMETER_NAME);
        criteriaQuery.where(cb.equal(root.get(Movement_.account)
            .get(Account_.id), accountIdParamaeter));
        return getEntityManager().createQuery(criteriaQuery);
    }

    @Override
    public List<Movement> findAllMovementsBySliceId(@NotNull Integer sliceId) {
        // TODO Auto-generated method stub
        // return null;
        throw new UnsupportedOperationException();
    }

    @Override
    protected Class<Movement> getEntityClass() {
        return Movement.class;
    }

}

package com.hrrm.famoney.application.service.accounts.internal;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.transaction.control.TransactionControl;

import com.hrrm.famoney.application.service.accounts.MovementsProcesor;
import com.hrrm.famoney.domain.accounts.movement.Movement;
import com.hrrm.famoney.domain.accounts.movement.MovementSlice;
import com.hrrm.famoney.domain.accounts.movement.repository.MovementRepository;
import com.hrrm.famoney.domain.accounts.movement.repository.MovementSliceRepository;

@Component(name = "MovementsProcessorByMovementDate", service = MovementsProcesor.class)
public class MovementsProcessorByMovementDateImpl extends MovementsProcessorImpl implements MovementsProcesor {

    private final MovementSliceRepository movementSliceRepository;
    private final MovementRepository movementRepository;

    @Activate
    public MovementsProcessorByMovementDateImpl(@Reference final TransactionControl txControl,
            @Reference final MovementSliceRepository movementSliceRepository,
            @Reference final MovementRepository movementRepository) {
        super(txControl);
        this.movementSliceRepository = movementSliceRepository;
        this.movementRepository = movementRepository;
    }

    @Override
    protected Optional<MovementSlice> findLastByAccountBeforeOffsetByDate(final Integer accountId,
            final Integer offset) {
        return movementSliceRepository.findLastByAccountBeforeOffsetByMovementDate(accountId,
                offset);
    }

    @Override
    protected List<Movement> findMovementsByAccountIdAfterDate(final Integer accountId, final LocalDateTime dateFrom,
            final Optional<Integer> limitFromSliceOptional) {
        return movementRepository.findMovementsByAccountIdAfterMovementDate(accountId,
                dateFrom,
                limitFromSliceOptional);
    }

    @Override
    protected Integer getCount(final MovementSlice movementSlice) {
        return movementSlice.getMovementCount();
    }

    @Override
    protected BigDecimal getSum(final MovementSlice movementSlice) {
        return movementSlice.getMovementSum();
    }

}

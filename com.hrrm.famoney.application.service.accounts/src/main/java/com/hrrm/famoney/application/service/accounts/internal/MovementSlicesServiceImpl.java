package com.hrrm.famoney.application.service.accounts.internal;

import java.time.LocalDateTime;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;
import org.osgi.service.transaction.control.TransactionControl;

import com.hrrm.famoney.application.service.accounts.MovementSlicesService;
import com.hrrm.famoney.domain.accounts.movement.MovementSlice;
import com.hrrm.famoney.domain.accounts.movement.repository.MovementSliceRepository;

@Component
public class MovementSlicesServiceImpl implements MovementSlicesService {

    private final Logger logger;
    private final MovementSliceRepository movementSliceRepository;
    private final TransactionControl txControl;

    @Activate
    public MovementSlicesServiceImpl(@Reference(service = LoggerFactory.class) final Logger logger,
            @Reference final MovementSliceRepository movementSliceRepository,
            @Reference final TransactionControl txControl) {
        this.logger = logger;
        this.movementSliceRepository = movementSliceRepository;
        this.txControl = txControl;
    }

    @Override
    public void rebalanceSlicesByMovementDate(final Integer accountId, final LocalDateTime date) {
        txControl.required(() -> {
            final var movementSlice = movementSliceRepository.findFirstByAccountIdAfterDate(accountId,
                    date);
            movementSlice.ifPresent(this::rebalanceSlice);
            return true;
        });
    }

    @Override
    public void rebalanceSlice(final MovementSlice movementSlice) {
        txControl.required(() -> {
            final var mergedMovementSlice = movementSliceRepository.save(movementSlice);

            mergedMovementSlice.getCount();

            return true;
        });
    }

}

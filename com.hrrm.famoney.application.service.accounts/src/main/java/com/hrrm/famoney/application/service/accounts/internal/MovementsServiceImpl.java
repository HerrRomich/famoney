package com.hrrm.famoney.application.service.accounts.internal;

import javax.persistence.LockModeType;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;
import org.osgi.service.transaction.control.TransactionControl;

import com.hrrm.famoney.application.service.accounts.MovementSlicesService;
import com.hrrm.famoney.application.service.accounts.MovementsService;
import com.hrrm.famoney.domain.accounts.movement.Movement;
import com.hrrm.famoney.domain.accounts.movement.repository.MovementRepository;
import com.hrrm.famoney.domain.accounts.movement.repository.MovementSliceRepository;
import com.hrrm.famoney.domain.accounts.repository.AccountRepository;

@Component
public class MovementsServiceImpl implements MovementsService {

    private final Logger logger;
    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;
    private final MovementSliceRepository movementSliceRepository;
    private final MovementSlicesService movementSlicesService;
    private final TransactionControl txControl;

    @Activate
    public MovementsServiceImpl(@Reference(service = LoggerFactory.class) final Logger logger,
            @Reference final AccountRepository accountRepository,
            @Reference final MovementRepository movementRepository,
            @Reference final MovementSliceRepository movementSliceRepository,
            @Reference final MovementSlicesService movementSlicesService,
            @Reference final TransactionControl txControl) {
        this.logger = logger;
        this.accountRepository = accountRepository;
        this.movementRepository = movementRepository;
        this.movementSliceRepository = movementSliceRepository;
        this.movementSlicesService = movementSlicesService;
        this.txControl = txControl;
    }

    @Override
    public Movement addMovement(Movement movement) {
        return txControl.required(() -> {
            var savedMovement = movementRepository.save(movement);
            var account = savedMovement.getAccount();
            accountRepository.lock(account,
                    LockModeType.PESSIMISTIC_WRITE);
            account.setMovementCount(account.getMovementCount() + 1);
            account.setMovementSum(account.getMovementSum()
                .add(movement.getAmount()));

            var movementSliceAfterDate = movementSliceRepository.findFirstByAccountIdAfterDate(account.getId(),
                    movement.getDate());
            movementSliceAfterDate.ifPresent(movementSlice -> {
                movementSlice.setMovementCount(movementSlice.getMovementCount() + 1);
                movementSlice.setMovementSum(movementSlice.getMovementSum()
                    .add(movement.getAmount()));
            });
            movementSlicesService.rebalanceSicesByMovementDate(account.getId(),
                    movement.getDate());
            return savedMovement;
        });
    }

}

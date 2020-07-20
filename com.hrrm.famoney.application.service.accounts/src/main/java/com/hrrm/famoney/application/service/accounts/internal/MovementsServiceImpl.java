package com.hrrm.famoney.application.service.accounts.internal;

import java.math.BigDecimal;

import javax.persistence.LockModeType;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;
import org.osgi.service.transaction.control.TransactionControl;

import com.hrrm.famoney.application.service.accounts.MovementsService;
import com.hrrm.famoney.domain.accounts.movement.Movement;
import com.hrrm.famoney.domain.accounts.movement.repository.MovementRepository;
import com.hrrm.famoney.domain.accounts.repository.AccountRepository;

@Component
public class MovementsServiceImpl implements MovementsService {

    private final Logger logger;
    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;
    private final TransactionControl txControl;

    @Activate
    public MovementsServiceImpl(@Reference(service = LoggerFactory.class) final Logger logger,
            @Reference final AccountRepository accountRepository,
            @Reference final MovementRepository movementRepository, @Reference final TransactionControl txControl) {
        this.logger = logger;
        this.accountRepository = accountRepository;
        this.movementRepository = movementRepository;
        this.txControl = txControl;
    }

    @Override
    public Movement addMovement(Movement movement) {
        logger.debug("Adding movement.");
        logger.trace("Adding movement {}.",
                movement);
        return txControl.required(() -> {
            var savedMovement = movementRepository.save(movement);
            var account = savedMovement.getAccount();
            accountRepository.lock(account,
                    LockModeType.PESSIMISTIC_WRITE);
            account.setMovementCount(account.getMovementCount() + 1);
            account.setMovementTotal(account.getMovementTotal()
                .add(savedMovement.getAmount()));
            movementRepository.flush();
            var totalBeforeMovement = movementRepository.findNextMovementByAccountIdBeforeDate(account.getId(),
                    savedMovement.getId())
                .map(Movement::getTotal)
                .orElse(BigDecimal.ZERO);
            savedMovement.setTotal(totalBeforeMovement.add(savedMovement.getAmount()));
            movementRepository.adjustMovementSumsByAccountIdAfterDate(account.getId(),
                    savedMovement.getDate(),
                    savedMovement.getAmount());
            accountRepository.lock(account,
                    LockModeType.NONE);
            return savedMovement;
        });
    }

}

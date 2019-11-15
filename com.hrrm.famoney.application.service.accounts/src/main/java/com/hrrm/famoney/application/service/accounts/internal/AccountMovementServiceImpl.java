package com.hrrm.famoney.application.service.accounts.internal;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import com.hrrm.famoney.application.service.accounts.AccountMovementService;
import com.hrrm.famoney.application.service.accounts.MovementSliceNotFound;
import com.hrrm.famoney.domain.accounts.movement.Movement;
import com.hrrm.famoney.domain.accounts.movement.MovementSlice;
import com.hrrm.famoney.domain.accounts.movement.repository.MovementRepository;
import com.hrrm.famoney.domain.accounts.movement.repository.MovementSliceRepository;

@Component(service = AccountMovementService.class, scope = ServiceScope.SINGLETON)
public class AccountMovementServiceImpl implements AccountMovementService {

    private final Logger logger;
    private final MovementSliceRepository movementSliceRepository;
    private final MovementRepository movementRepository;

    @Activate
    public AccountMovementServiceImpl(@Reference(service = LoggerFactory.class) Logger logger,
            @Reference MovementSliceRepository movementSliceRepository,
            @Reference MovementRepository movementRepository) {
        super();
        this.logger = logger;
        this.movementSliceRepository = movementSliceRepository;
        this.movementRepository = movementRepository;
    }

    @Override
    public List<Movement> findAllMovementsBySliceId(@NotNull Integer accountId,
        @NotNull Integer sliceId) throws MovementSliceNotFound {
        logger.debug("Searching for all movements in account inside specified slice.");
        logger.trace(
            "Searching for all movements in account with id: {} inside specified slice with id: {}.",
            accountId, sliceId);
        LocalDateTime dateFrom;
        if (sliceId == MovementSlice.FIRST_SLICE_ID) {
            dateFrom = MovementSlice.FIRST_SLICE_DATE;
        } else {
            dateFrom = movementSliceRepository.find(sliceId)
                .filter(slice -> accountId.equals(slice.getAccount()
                    .getId()))
                .map(MovementSlice::getDate)
                .orElseThrow(() -> new MovementSliceNotFound(MessageFormat.format(
                    "Movement slice with id: {0} is not found in account with id: {1}.", sliceId,
                    accountId)));
        }
        var dateTo = movementSliceRepository.findFirstByAccountIdAfterDate(accountId, dateFrom)
            .map(MovementSlice::getDate)
            .orElse(MovementSlice.LAST_SLICE_DATE);
        return movementRepository.findByAccountIdBetweenDates(accountId, dateFrom, dateTo);
    }

}

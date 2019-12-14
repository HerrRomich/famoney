package com.hrrm.famoney.application.service.accounts.internal;

import java.text.MessageFormat;
import java.time.LocalDate;
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
    public List<Movement> findAllMovementsBySliceIdOverDate(@NotNull Integer accountId,
            @NotNull Integer sliceId) throws MovementSliceNotFound {
        logger.debug("Searching for all movements in account with id: {} " +
                "inside specified slice with id: {}.", accountId, sliceId);
        var dateFrom = getSliceDateFrom(accountId, sliceId);
        var dateTo = movementSliceRepository.findFirstByAccountIdAfterDate(accountId, dateFrom)
            .map(MovementSlice::getDate)
            .orElse(MovementSlice.LAST_SLICE_DATE)
            .atTime(0, 0);
        List<Movement> movementsByAccountIdBetweenDates = movementRepository
            .findByAccountIdBetweenDates(accountId, dateFrom, dateTo);
        logger.debug("Successfully found {} movements in account with id: {} " +
                "inside specified slice with id: {}.", movementsByAccountIdBetweenDates.size(),
                accountId, sliceId);
        logger.trace("Successfully found {} movements in account with id: {} " +
                "inside specified slice with id: {}\r\n{}.", accountId, sliceId,
                movementsByAccountIdBetweenDates);
        return movementsByAccountIdBetweenDates;
    }

    @Override
    public List<Movement> findAllMovementsBySliceIdOverBookingDate(@NotNull Integer accountId,
            @NotNull Integer sliceId) throws MovementSliceNotFound {
        logger.debug("Searching for all movements in account with id: {} " +
                "inside specified slice with id: {}.", accountId, sliceId);
        var dateFrom = getSliceDateFrom(accountId, sliceId);
        var dateTo = movementSliceRepository.findFirstByAccountIdAfterDate(accountId, dateFrom)
            .map(MovementSlice::getDate)
            .orElse(MovementSlice.LAST_SLICE_DATE)
            .atTime(0, 0);
        List<Movement> movementsByAccountIdBetweenDates = movementRepository
            .findByAccountIdBetweenBookingDates(accountId, dateFrom, dateTo);
        logger.debug("Successfully found {} movements in account with id: {} " +
                "inside specified slice with id: {}.", movementsByAccountIdBetweenDates.size(),
                accountId, sliceId);
        logger.trace(l -> l.trace("Successfully found {} movements in account with id: {} " +
                "inside specified slice with id: {}\r\n{}.", accountId, sliceId,
                movementsByAccountIdBetweenDates));
        return movementsByAccountIdBetweenDates;
    }

    private LocalDateTime getSliceDateFrom(Integer accountId, Integer sliceId)
            throws MovementSliceNotFound {
        LocalDate dateFrom;
        if (MovementSlice.FIRST_SLICE_ID.equals(sliceId)) {
            dateFrom = MovementSlice.FIRST_SLICE_DATE;
        } else {
            dateFrom = movementSliceRepository.find(sliceId)
                .filter(slice -> accountId.equals(slice.getAccount()
                    .getId()))
                .map(MovementSlice::getDate)
                .orElseThrow(() -> new MovementSliceNotFound(MessageFormat.format(
                        "Movement slice with id: {0} is not found in account with id: {1}.",
                        sliceId, accountId)));
        }
        return dateFrom.atTime(0, 0);
    }

}

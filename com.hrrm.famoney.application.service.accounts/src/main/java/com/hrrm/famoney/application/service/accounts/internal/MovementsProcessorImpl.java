package com.hrrm.famoney.application.service.accounts.internal;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;
import org.osgi.service.transaction.control.TransactionControl;

import com.hrrm.famoney.application.service.accounts.MovementsProcesor;
import com.hrrm.famoney.domain.accounts.movement.Movement;
import com.hrrm.famoney.domain.accounts.movement.MovementSlice;
import com.hrrm.famoney.domain.accounts.movement.repository.MovementRepository;
import com.hrrm.famoney.domain.accounts.movement.repository.MovementSliceRepository;

@Component(name = "MovementsProcessor", service = MovementsProcesor.class)
public class MovementsProcessorImpl implements MovementsProcesor {

    private final Logger logger;
    private final MovementSliceRepository movementSliceRepository;
    private final MovementRepository movementRepository;
    protected final TransactionControl txControl;

    @Activate
    public MovementsProcessorImpl(@Reference(service = LoggerFactory.class) final Logger logger,
            @Reference final TransactionControl txControl,
            @Reference final MovementSliceRepository movementSliceRepository,
            @Reference final MovementRepository movementRepository) {
        this.logger = logger;
        this.movementSliceRepository = movementSliceRepository;
        this.movementRepository = movementRepository;
        this.txControl = txControl;
    }

    @Override
    public AccountMovementsResponse getMovementsByOffsetAndLimit(
            final AccountMovementsRequestByOffsetAndLimit request) {
        logger.debug("Searching movements by offset and limit.");
        logger.trace(l -> l.trace("Searching movements by offset {} and limit {}.",
                request.getOffset()
                    .map(offset -> offset.toString())
                    .orElse("\"from beginning\""),
                request.getLimit()
                    .map(limit -> limit.toString())
                    .orElse("\"unlimited\"")));
        return txControl.required(() -> {
            final var movementSliceOptional = request.getOffset()
                .flatMap(offsetVal -> movementSliceRepository.findLastByAccountBeforeOffsetByDate(request
                    .getAccountId(),
                        offsetVal));
            final var dateFrom = movementSliceOptional.map(MovementSlice::getDate)
                .orElse(MovementSlice.FIRST_SLICE_DATE);
            final var offsetFromSlice = request.getOffset()
                .orElse(0) -
                movementSliceOptional.map(MovementSlice::getCount)
                    .orElse(0);
            final var limitFromSliceOptional = request.getLimit()
                .map(limitVal -> limitVal + offsetFromSlice);
            final var movements = movementRepository.findMovementsByAccountIdAfterDate(request.getAccountId(),
                    dateFrom,
                    limitFromSliceOptional)
                .stream()
                .skip(offsetFromSlice)
                .collect(Collectors.toList());
            final var sum = Stream.concat(movementSliceOptional.map(MovementSlice::getSum)
                .stream(),
                    movements.stream()
                        .limit(offsetFromSlice)
                        .map(Movement::getAmount))
                .reduce(BigDecimal.ZERO,
                        BigDecimal::add);
            logger.debug("Successfully found {} movement(s) by offset and limit.",
                    movements.size());
            logger.trace(l -> l.trace("Successfully found movements by offset and limit: {}.",
                    movements));
            return AccountMovementsResponse.builder()
                .startSum(sum)
                .movements(movements)
                .build();
        });
    }

    @Override
    public AccountMovementResponse getMovementById(AccountMovementRequestById request) {
        logger.debug("Searching movement in account by id.");
        logger.trace("Searching movement with id));
        return txControl.required(() -> {
            var movement = movementRepository.find(request.getMovementId())
                    .orElseThrow(() -> new MovementNotFound(MessageFormat.format("A movement with id: {0} is not found.", request.getMovementId());
            movementSliceRepository.findFirstByAccountIdAfterDate(request.getAccountId(),
                    null);
            return AccountMovementResponse.builder()
                .build();
        });
    }

}

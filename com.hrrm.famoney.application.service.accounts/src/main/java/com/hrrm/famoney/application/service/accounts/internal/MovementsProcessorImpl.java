package com.hrrm.famoney.application.service.accounts.internal;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.constraints.NotNull;

import org.osgi.service.transaction.control.TransactionControl;

import com.hrrm.famoney.application.service.accounts.MovementsProcesor;
import com.hrrm.famoney.application.service.accounts.dataobject.AccountMovementsRequest;
import com.hrrm.famoney.application.service.accounts.dataobject.AccountMovementsResponse;
import com.hrrm.famoney.application.service.accounts.dataobject.impl.AccountMovementsResponseImpl;
import com.hrrm.famoney.domain.accounts.movement.Movement;
import com.hrrm.famoney.domain.accounts.movement.MovementSlice;

public abstract class MovementsProcessorImpl implements MovementsProcesor {

    protected final TransactionControl txControl;

    public MovementsProcessorImpl(final TransactionControl txControl) {
        super();
        this.txControl = txControl;
    }

    @Override
    public AccountMovementsResponse getMovementsSlice(final AccountMovementsRequest request) {
        return txControl.required(() -> {
            final var movementSliceOptional = request.getOffset()
                .flatMap(offsetVal -> findLastByAccountBeforeOffsetByDate(request.getAccountId(),
                        offsetVal));
            final var dateFrom = movementSliceOptional.map(MovementSlice::getDate)
                .orElse(MovementSlice.FIRST_SLICE_DATE)
                .atTime(0,
                        0);
            final var offsetFromSlice = request.getOffset()
                .orElse(0) -
                movementSliceOptional.map(this::getCount)
                    .orElse(0);
            final var limitFromSliceOptional = request.getLimit()
                .map(limitVal -> limitVal + offsetFromSlice);
            final List<Movement> movements = findMovementsByAccountIdAfterDate(request.getAccountId(),
                    dateFrom,
                    limitFromSliceOptional).stream()
                        .skip(offsetFromSlice)
                        .collect(Collectors.toList());
            final var sum = Stream.concat(movementSliceOptional.map(this::getSum)
                .stream(),
                    movements.stream()
                        .limit(offsetFromSlice)
                        .map(Movement::getAmount))
                .reduce(BigDecimal.ZERO,
                        BigDecimal::add);
            return AccountMovementsResponseImpl.builder()
                .startSum(sum)
                .movements(movements)
                .build();
        });
    }

    protected abstract Optional<MovementSlice> findLastByAccountBeforeOffsetByDate(@NotNull Integer accountId,
            Integer offset);

    protected abstract List<Movement> findMovementsByAccountIdAfterDate(@NotNull Integer accountId,
            LocalDateTime dateFrom, Optional<Integer> limitFromSliceOptional);

    protected abstract Integer getCount(MovementSlice movementSlice);

    protected abstract BigDecimal getSum(MovementSlice movementSlice);

}

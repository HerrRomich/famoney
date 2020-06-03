package com.hrrm.famoney.application.service.accounts;

import java.time.LocalDateTime;

import com.hrrm.famoney.domain.accounts.movement.MovementSlice;

public interface MovementSlicesService {

    void rebalanceSlicesByMovementDate(final Integer accountId, final LocalDateTime date);

    void rebalanceSlice(final MovementSlice movementSlice);

}

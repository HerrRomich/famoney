package com.hrrm.famoney.application.service.accounts;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.hrrm.famoney.domain.accounts.movement.Movement;

public interface AccountMovementService {

    List<Movement> findAllMovementsBySliceIdOverDate(@NotNull Integer accountId,
            @NotNull Integer sliceId) throws MovementSliceNotFound;

    List<Movement> findAllMovementsBySliceIdOverBookingDate(@NotNull Integer accountId,
            @NotNull Integer sliceId) throws MovementSliceNotFound;

}

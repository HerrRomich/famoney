package com.hrrm.famoney.application.service.accounts;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.hrrm.famoney.domain.accounts.movement.Movement;

public interface AccountMovementService {

    List<Movement> findAllMovementsBySliceId(@NotNull Integer accountId, @NotNull Integer sliceId)
            throws MovementSliceNotFound;

}

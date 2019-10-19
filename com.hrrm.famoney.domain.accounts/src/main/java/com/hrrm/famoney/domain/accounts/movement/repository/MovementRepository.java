package com.hrrm.famoney.domain.accounts.movement.repository;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.hrrm.famoney.domain.accounts.movement.Movement;
import com.hrrm.famoney.domain.accounts.repository.AccountsDomainRepository;

public interface MovementRepository extends AccountsDomainRepository<Movement> {

    List<Movement> findMovementsByAccountId(@NotNull Integer accountId);

    List<Movement> findAllMovementsBySliceId(@NotNull Integer sliceId);

}

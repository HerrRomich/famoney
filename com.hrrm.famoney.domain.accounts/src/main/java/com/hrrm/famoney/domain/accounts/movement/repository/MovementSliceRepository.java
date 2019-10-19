package com.hrrm.famoney.domain.accounts.movement.repository;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.hrrm.famoney.domain.accounts.movement.MovementSlice;
import com.hrrm.famoney.domain.accounts.repository.AccountsDomainRepository;

public interface MovementSliceRepository extends
        AccountsDomainRepository<MovementSlice> {

    List<MovementSlice> getMovementSlicesByAccountId(
            @NotNull Integer accountId);

}

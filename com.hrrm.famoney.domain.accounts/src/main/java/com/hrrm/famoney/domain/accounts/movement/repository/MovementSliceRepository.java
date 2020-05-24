package com.hrrm.famoney.domain.accounts.movement.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import com.hrrm.famoney.domain.accounts.movement.MovementSlice;
import com.hrrm.famoney.domain.accounts.repository.AccountsDomainRepository;

public interface MovementSliceRepository extends AccountsDomainRepository<MovementSlice> {

    List<MovementSlice> getMovementSlicesByAccountId(@NotNull Integer accountId);

    Optional<MovementSlice> findFirstByAccountIdAfterDate(@NotNull Integer accountId, @NotNull LocalDateTime dateFrom);

    Optional<MovementSlice> findLastByAccountBeforeOffsetByMovementDate(@NotNull Integer accountId,
            @NotNull Integer offset);

    Optional<MovementSlice> findLastByAccountBeforeOffsetByBookingDate(@NotNull Integer accountId,
            @NotNull Integer offset);

}

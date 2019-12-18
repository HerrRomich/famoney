package com.hrrm.famoney.domain.accounts.movement.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import com.hrrm.famoney.domain.accounts.movement.Movement;
import com.hrrm.famoney.domain.accounts.repository.AccountsDomainRepository;

public interface MovementRepository extends AccountsDomainRepository<Movement> {

    List<Movement> findByAccountIdBetweenDates(@NotNull Integer accountId, LocalDateTime dateFrom,
            LocalDateTime dateTo);

    List<Movement> findByAccountIdBetweenBookingDates(@NotNull Integer accountId,
            LocalDateTime dateFrom, LocalDateTime dateTo);

    Long getMoventsCountByAccountId(@NotNull Integer accountId);

    List<Movement> findMovementsByAccountIdAfterMovementDate(@NotNull Integer accountId,
            LocalDateTime dateFrom, Optional<Integer> limitOptional);

    List<Movement> findMovementsByAccountIdAfterBookingDate(@NotNull Integer accountId,
            LocalDateTime dateFrom, Optional<Integer> limitOptional);

}

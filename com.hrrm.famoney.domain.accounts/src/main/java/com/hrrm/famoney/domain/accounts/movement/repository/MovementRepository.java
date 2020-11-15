package com.hrrm.famoney.domain.accounts.movement.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.hrrm.famoney.domain.accounts.Account;
import com.hrrm.famoney.domain.accounts.movement.Movement;
import com.hrrm.famoney.domain.accounts.repository.AccountsDomainRepository;

public interface MovementRepository extends AccountsDomainRepository<Movement> {

    List<Movement> findByAccountBetweenDates(Account account, LocalDateTime dateFrom,
            LocalDateTime dateTo);

    List<Movement> findByAccountBetweenBookingDates(Account account, LocalDateTime dateFrom,
            LocalDateTime dateTo);

    Long getMoventsCountByAccount(Account account);

    List<Movement> findMovementsByAccountAfterDate(Account account, LocalDateTime dateFrom, Integer limit);

    List<Movement> findMovementsByAccountAfterBookingDate(Account account, LocalDateTime dateFrom,
            Integer limit);

    Optional<Movement> findNextMovementByAccountIdBeforeDate(Account account, LocalDateTime date);

    Optional<Movement> findNextMovementByAccountIdBeforeBookingDate(Account account, LocalDateTime date);

    void adjustMovementSumsByAccountAfterDate(Account account, LocalDateTime fromDate, BigDecimal amount);

    List<Movement> getMovementsByAccountIdWithOffsetAndLimitOrderedByDate(Account account, Integer offset,
            Integer limit);

}

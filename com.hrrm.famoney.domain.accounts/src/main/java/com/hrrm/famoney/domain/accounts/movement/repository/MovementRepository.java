package com.hrrm.famoney.domain.accounts.movement.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.hrrm.famoney.domain.accounts.Account;
import com.hrrm.famoney.domain.accounts.movement.Movement;
import com.hrrm.famoney.domain.accounts.repository.AccountsDomainRepository;

public interface MovementRepository extends AccountsDomainRepository<Movement> {

    Long getMovementsCountByAccount(Account account);

    Optional<Movement> findNextMovementByAccountIdBeforePosition(Account account, Integer position);

    void rollbackMovementPositionsAndSumsByAccountAfterPosition(Movement movement, Integer positionBefore);

    void adjustMovementPositionsAndSumsByAccountAfterPosition(Movement movement, Integer positionAfter);

    List<Movement> getMovementsByAccountIdWithOffsetAndLimitOrderedByPos(Account account, Integer offset,
            Integer limit);

    Integer getLastPositionByAccountOnDate(Account account, LocalDate date);

}

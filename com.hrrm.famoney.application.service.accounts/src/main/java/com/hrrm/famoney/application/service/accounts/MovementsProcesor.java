package com.hrrm.famoney.application.service.accounts;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.hrrm.famoney.domain.accounts.movement.Movement;

import lombok.Builder;
import lombok.Getter;

public interface MovementsProcesor {

    AccountMovementsResponse getMovementsByOffsetAndLimit(AccountMovementsRequestByOffsetAndLimit request);

    AccountMovementResponse getMovementById(AccountMovementRequestById request);

    @Builder
    @Getter
    public static class AccountMovementsRequestByOffsetAndLimit {

        private final Integer accountId;
        private final Integer offset;
        private final Integer limit;

        public Optional<Integer> getOffset() {
            return Optional.ofNullable(offset);
        }

        public Optional<Integer> getLimit() {
            return Optional.ofNullable(limit);
        }

    }

    @Builder
    @Getter
    public static class AccountMovementsResponse {
        BigDecimal startSum;
        List<Movement> movements;
    }

    @Builder
    @Getter
    public static class AccountMovementRequestById {
        private final Integer accountId;
        private final Integer movementId;
    }

    @Builder
    @Getter
    public static class AccountMovementResponse {
        BigDecimal startSum;
        Movement movement;
    }

}

package com.hrrm.famoney.domain.accounts.migrations.v02;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.immutables.value.Value;

@Value.Immutable
public interface AccountMovementData {

    int getAccountId();

    LocalDateTime getDate();

    LocalDateTime getBookingDate();

    BigDecimal getAmount();

}

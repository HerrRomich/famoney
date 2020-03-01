package com.hrrm.famoney.application.service.accounts.dataobject;

import java.math.BigDecimal;
import java.util.List;

import org.immutables.value.Value;

import com.hrrm.famoney.domain.accounts.movement.Movement;

@Value.Immutable
public interface AccountMovementsResponse {

    BigDecimal getStartSum();

    List<Movement> getMovements();

}

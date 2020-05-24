package com.hrrm.famoney.application.service.accounts;

import java.time.LocalDateTime;

public interface MovementSlicesService {

    void rebalanceSicesByMovementDate(Integer id, LocalDateTime date);

}

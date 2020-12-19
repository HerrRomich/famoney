package com.hrrm.famoney.infrastructure.jaxrs;

import java.time.LocalDateTime;

public interface OperationTimestampProvider {

    void setTimestamp();

    LocalDateTime getTimestamp();

}

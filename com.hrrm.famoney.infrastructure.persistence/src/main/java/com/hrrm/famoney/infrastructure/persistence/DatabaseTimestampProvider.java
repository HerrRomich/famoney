package com.hrrm.famoney.infrastructure.persistence;

import java.sql.SQLException;
import java.time.LocalDateTime;

public interface DatabaseTimestampProvider {
    
    LocalDateTime getTimestamp() throws SQLException;

}

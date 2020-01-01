package com.hrrm.famoney.infrastructure.persistence;

import java.io.Serializable;

public interface DomainEntity<T extends Serializable> {

    T getId();

}

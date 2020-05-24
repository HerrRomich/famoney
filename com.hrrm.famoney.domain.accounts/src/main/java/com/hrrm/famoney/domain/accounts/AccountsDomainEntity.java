package com.hrrm.famoney.domain.accounts;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.hrrm.famoney.infrastructure.persistence.DomainEntity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@EqualsAndHashCode
@Getter
@SuperBuilder
public abstract class AccountsDomainEntity implements DomainEntity<Integer> {

    public static final String SCHEMA_NAME = "accounts";

    @Id
    @Column(name = "id")
    private Integer id;

}

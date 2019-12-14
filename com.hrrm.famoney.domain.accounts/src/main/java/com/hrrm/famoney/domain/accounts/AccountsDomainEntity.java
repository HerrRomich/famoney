package com.hrrm.famoney.domain.accounts;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.hrrm.infrastructure.persistence.DomainEntity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@MappedSuperclass
@EqualsAndHashCode
@Getter
public abstract class AccountsDomainEntity implements DomainEntity<Integer> {

    public static final String ACCOUNTS_SCHEMA_NAME = "famoney_accounts";

    @Id
    @Column(name = "id")
    private Integer id;

}

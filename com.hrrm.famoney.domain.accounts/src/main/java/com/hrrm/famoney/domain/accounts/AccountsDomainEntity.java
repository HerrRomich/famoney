package com.hrrm.famoney.domain.accounts;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.hrrm.famoney.infrastructure.persistence.DomainEntity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@MappedSuperclass
@EqualsAndHashCode
@Getter
@ToString
public abstract class AccountsDomainEntity implements DomainEntity<Integer> {

    public static final String SCHEMA_NAME = "accounts";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

}

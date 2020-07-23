package com.hrrm.famoney.domain.masterdata;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.hrrm.famoney.infrastructure.persistence.DomainEntity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@MappedSuperclass
@EqualsAndHashCode
@Getter
public abstract class MasterDataDomainEntity implements DomainEntity<Integer> {

    public static final String SCHEMA_NAME = "master_data";

    @Id
    @Column(name = "id")
    private Integer id;

}

package com.hrrm.famoney.domain.datadirectory;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.hrrm.famoney.infrastructure.persistence.DomainEntity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@MappedSuperclass
@EqualsAndHashCode
@Getter
public abstract class DataDirectoryDomainEntity implements DomainEntity<Integer> {

    public static final String SCHEMA_NAME = "data_directory";

    @Id
    @Column(name = "id")
    private Integer id;

}

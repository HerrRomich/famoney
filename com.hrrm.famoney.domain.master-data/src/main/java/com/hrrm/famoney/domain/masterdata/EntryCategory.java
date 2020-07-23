package com.hrrm.famoney.domain.masterdata;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Entity
@Table(schema = MasterDataDomainEntity.SCHEMA_NAME, name = "entry_category")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "category_type")
@Accessors(chain = true)
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public abstract class EntryCategory<T extends EntryCategory<T>> extends MasterDataDomainEntity {

    @Column(name = "name")
    private String name;

    public abstract T getParent();

    public abstract EntryCategory<T> setParent(T parent);

    public abstract List<T> getChildren();

    public abstract EntryCategory<T> setChildren(List<T> children);

}

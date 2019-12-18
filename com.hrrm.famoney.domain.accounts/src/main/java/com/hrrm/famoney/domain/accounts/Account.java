package com.hrrm.famoney.domain.accounts;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Entity
@Table(schema = AccountsDomainEntity.ACCOUNTS_SCHEMA_NAME, name = "account")
@Accessors(chain = true)
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Account extends AccountsDomainEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "open_date")
    private LocalDateTime openDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "tag")
    @CollectionTable(schema = AccountsDomainEntity.ACCOUNTS_SCHEMA_NAME, name = "account_tag",
        joinColumns = { @JoinColumn(name = "account_id") })
    private Set<String> tags;

    @Column(name = "movement_count")
    private Integer movementCount;

    @Column(name = "movement_sum")
    private BigDecimal movementSum;

}

package com.hrrm.famoney.domain.accounts.movement;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.hrrm.famoney.domain.accounts.Account;
import com.hrrm.famoney.domain.accounts.AccountsDomainEntity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Entity
@Table(schema = AccountsDomainEntity.SCHEMA_NAME, name = "movement_slice")
@Accessors(chain = true)
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@SuperBuilder
public class MovementSlice extends AccountsDomainEntity {

    public static final Integer FIRST_SLICE_ID = -1;
    public static final LocalDate FIRST_SLICE_DATE = LocalDate.of(1970,
            1,
            1);
    public static final LocalDate LAST_SLICE_DATE = LocalDate.of(2070,
            1,
            1);

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "count")
    private Integer movementCount;

    @Column(name = "sum")
    private BigDecimal movementSum;

}

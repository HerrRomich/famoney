package com.hrrm.famoney.domain.accounts.movement;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

@Entity
@Table(schema = AccountsDomainEntity.SCHEMA_NAME, name = "movement_slice")
@Accessors(chain = true)
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class MovementSlice extends AccountsDomainEntity {

    public static final Integer FIRST_SLICE_ID = -1;
    public static final LocalDateTime FIRST_SLICE_DATE = LocalDateTime.of(1970,
            1,
            1,
            0,
            0);
    public static final LocalDateTime LAST_SLICE_DATE = LocalDateTime.of(2070,
            1,
            1,
            0,
            0);

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "count")
    private Integer count;

    @Column(name = "sum")
    private BigDecimal sum;

}

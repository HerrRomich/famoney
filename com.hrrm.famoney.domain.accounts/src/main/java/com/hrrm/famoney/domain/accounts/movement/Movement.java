package com.hrrm.famoney.domain.accounts.movement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(schema = AccountsDomainEntity.SCHEMA_NAME, name = "movement")
@DiscriminatorColumn(name = "type")
@Accessors(chain = true)
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public abstract class Movement extends AccountsDomainEntity {

    public static final String FIND_MOVEMENTS_WITH_PAGINATION = "com.hrrm.famoney.domain.accounts.movement.AccountMovement#finMovenetsWithPagination";
    public static final String ACCOUNT_ID_PARAMETER_NAME = "accountId";
    public static final String MOVEMENT_DATE_PARAMETER_NAME = "movementDateId";

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "booking_date")
    private LocalDateTime bookingDate;

    @Column(name = "budget_period")
    private LocalDate budgetPeriod;

    @Column(name = "amount")
    private BigDecimal amount;

}

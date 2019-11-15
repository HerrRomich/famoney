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

@Entity
@Table(schema = AccountsDomainEntity.ACCOUNTS_SCHEMA_NAME, name = "movement_slice")
public class MovementSlice extends AccountsDomainEntity {

    public static final Integer FIRST_SLICE_ID = -1;
    public static final LocalDateTime FIRST_SLICE_DATE = LocalDateTime.MIN;
    public static final LocalDateTime LAST_SLICE_DATE = LocalDateTime.MAX;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "movement_count")
    private Integer movementCount;

    @Column(name = "movement_sum")
    private BigDecimal movementSum;

    @Column(name = "booking_count")
    private Integer bookingCount;

    @Column(name = "booking_sum")
    private BigDecimal bookingSum;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Integer getMovementCount() {
        return movementCount;
    }

    public void setMovementCount(Integer movementSliceCount) {
        this.movementCount = movementSliceCount;
    }

    public BigDecimal getMovementSum() {
        return movementSum;
    }

    public void setMovementSum(BigDecimal movementSliceSum) {
        this.movementSum = movementSliceSum;
    }

    public Integer getBookingCount() {
        return bookingCount;
    }

    public void setBookingCount(Integer bookingCount) {
        this.bookingCount = bookingCount;
    }

    public BigDecimal getBookingSum() {
        return bookingSum;
    }

    public void setBookingSum(BigDecimal bookingSum) {
        this.bookingSum = bookingSum;
    }

}

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

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "count")
    private Integer count;

    @Column(name = "sum")
    private BigDecimal sum;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime sliceDate) {
        this.date = sliceDate;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer movementSliceCount) {
        this.count = movementSliceCount;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal movementSliceSum) {
        this.sum = movementSliceSum;
    }

}

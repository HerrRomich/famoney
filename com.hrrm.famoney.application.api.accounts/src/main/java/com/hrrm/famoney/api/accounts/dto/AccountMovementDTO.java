package com.hrrm.famoney.api.accounts.dto;

import javax.annotation.processing.Generated;

import com.hrrm.famoney.infrastructure.jaxrs.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AccountMovement")
public class AccountMovementDTO implements DTO {

    private Integer id;
    private String accountLink;
    private Integer movementGroupId;
    private double amount;

    @Generated("SparkTools")
    private AccountMovementDTO(Builder builder) {
        this.id = builder.id;
        this.accountLink = builder.accountLink;
        this.movementGroupId = builder.movementGroupId;
        this.amount = builder.amount;
    }

    @Generated("SparkTools")
    public AccountMovementDTO() {
    }

    public Integer getId() {
        return id;
    }

    public String getAccountLink() {
        return accountLink;
    }

    public Integer getMovementGroupId() {
        return movementGroupId;
    }

    public double getAmount() {
        return amount;
    }

    @Generated("SparkTools")
    public static Builder builder() {
        return new Builder();
    }

    @Generated("SparkTools")
    public static Builder builderFrom(AccountMovementDTO accountMovementDTO) {
        return new Builder(accountMovementDTO);
    }

    @Generated("SparkTools")
    public static final class Builder {
        private Integer id;
        private String accountLink;
        private Integer movementGroupId;
        private double amount;

        private Builder() {
        }

        private Builder(AccountMovementDTO accountMovementDTO) {
            this.id = accountMovementDTO.id;
            this.accountLink = accountMovementDTO.accountLink;
            this.movementGroupId = accountMovementDTO.movementGroupId;
            this.amount = accountMovementDTO.amount;
        }

        public Builder Id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder AccountLink(String accountLink) {
            this.accountLink = accountLink;
            return this;
        }

        public Builder MovementGroupId(Integer movementGroupId) {
            this.movementGroupId = movementGroupId;
            return this;
        }

        public Builder Amount(double amount) {
            this.amount = amount;
            return this;
        }

        public AccountMovementDTO build() {
            return new AccountMovementDTO(this);
        }
    }

}

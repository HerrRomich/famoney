package com.hrrm.famoney.api.accounts.dto;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.processing.Generated;

import com.hrrm.famoney.infrastructure.jaxrs.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Account")
public class AccountDTO implements DTO {

    private Integer id;

    @Schema(required = true)
    private String name;

    private Collection<String> tags;

    @Generated("SparkTools")
    private AccountDTO(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.tags = builder.tags;
    }

    @Generated("SparkTools")
    public AccountDTO() {
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Collection<String> getTags() {
        return Collections.unmodifiableCollection(tags);
    }

    @Generated("SparkTools")
    public static Builder builder() {
        return new Builder();
    }

    @Generated("SparkTools")
    public static Builder builderFrom(AccountDTO accountDTO) {
        return new Builder(accountDTO);
    }

    @Generated("SparkTools")
    public static final class Builder {
        private Integer id;
        private String name;
        private Collection<String> tags = Collections.emptyList();

        private Builder() {
        }

        private Builder(AccountDTO accountDTO) {
            this.id = accountDTO.id;
            this.name = accountDTO.name;
            this.tags = accountDTO.tags;
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder tags(Collection<String> tags) {
            this.tags = tags;
            return this;
        }

        public AccountDTO build() {
            return new AccountDTO(this);
        }
    }

}

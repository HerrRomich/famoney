package com.hrrm.famoney.domain.accounts.movement;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Embeddable
@Accessors(chain = true)
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class EntryItem {

    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "comments")
    private String comments;

}

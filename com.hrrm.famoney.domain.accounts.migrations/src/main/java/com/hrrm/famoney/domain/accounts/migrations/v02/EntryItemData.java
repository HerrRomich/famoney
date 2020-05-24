package com.hrrm.famoney.domain.accounts.migrations.v02;

import java.math.BigDecimal;

import org.immutables.value.Value;

@Value.Immutable
public interface EntryItemData {

    Integer getCategoryId();

    BigDecimal getAmount();

    String getComments();

}

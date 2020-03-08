package com.hrrm.famoney.domain.accounts.migrations.v01;

import java.time.LocalDate;
import java.util.List;

import org.immutables.value.Value;

@Value.Immutable
public interface AccountData {

    int getBudgetId();

    String getName();

    LocalDate getOpenDate();

    List<String> getTags();

}

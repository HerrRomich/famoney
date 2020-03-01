package com.hrrm.famoney.domain.datadirectory.migration;

import java.util.OptionalInt;

import org.immutables.value.Value;

@Value.Immutable
public interface CategoryGroupData {

    String getType();

    OptionalInt getParentId();

}

package com.hrrm.famoney.domain.datadirectory.migrations.v1;

import java.util.OptionalInt;

import org.immutables.value.Value;

@Value.Immutable
public interface CategoryGroupData {

    String getType();

    OptionalInt getParentId();

}

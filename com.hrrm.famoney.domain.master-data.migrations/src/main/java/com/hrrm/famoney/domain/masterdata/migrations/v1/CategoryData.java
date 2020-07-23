package com.hrrm.famoney.domain.masterdata.migrations.v1;

import org.immutables.value.Value;

@Value.Immutable
public interface CategoryData extends CategoryGroupData {

    String getName();

}

package com.hrrm.famoney.domain.datadirectory.migration;

import org.immutables.value.Value;

@Value.Immutable
public interface CategoryData extends CategoryGroupData {

    String getName();

}

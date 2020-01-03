package com.hrrm.famoney.domain.datadirectory;

import javax.persistence.DiscriminatorValue;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@DiscriminatorValue("expense")
@Accessors(chain = true)
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class ExpenseCategory extends EntryCategory<ExpenseCategory> {

}

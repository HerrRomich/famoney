package com.hrrm.famoney.domain.datadictionary;

import javax.persistence.DiscriminatorValue;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@DiscriminatorValue("income")
@Accessors(chain = true)
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class IncomeCategory extends EntryCategory<IncomeCategory> {

}

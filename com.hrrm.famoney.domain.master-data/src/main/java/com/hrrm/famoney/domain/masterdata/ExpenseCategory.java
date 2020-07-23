package com.hrrm.famoney.domain.masterdata;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Entity
@DiscriminatorValue("expense")
@Accessors(chain = true)
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class ExpenseCategory extends EntryCategory<ExpenseCategory> {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    @BatchFetch(value = BatchFetchType.IN)
    private ExpenseCategory parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @BatchFetch(value = BatchFetchType.IN)
    private List<ExpenseCategory> children;

}

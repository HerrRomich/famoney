package com.hrrm.famoney.domain.accounts.movement;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;

import org.eclipse.persistence.annotations.JoinFetch;
import org.eclipse.persistence.annotations.JoinFetchType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Entity
@DiscriminatorValue("entry")
@Accessors(chain = true)
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Entry extends Movement {

    @ElementCollection
    @CollectionTable(name = "entry_item", joinColumns = @JoinColumn(name = "entry_id"))
    @JoinFetch(JoinFetchType.INNER)
    private List<EntryItem> entryItems = new ArrayList<>();

}

package com.hrrm.famoney.domain.accounts.movement;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("entry")
public class Entry extends Movement {

    @ManyToOne
    @JoinColumn(name = "entry_category_id")
    EntryCategory entryCategory;

}

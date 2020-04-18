package com.hrrm.famoney.domain.accounts.movement;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;

import lombok.AccessLevel;
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

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @ElementCollection
    @CollectionTable(name = "entry_item", joinColumns = @JoinColumn(name = "entry_id"))
    private List<EntryItem> entryItems;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Column(name = "category_id")
    private Integer categoryId;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Column(name = "comments")
    private String comments;

    public List<EntryItem> getEntryItems() {
        if (entryItems.isEmpty()) {
            return List.of(new EntryItem().setCategoryId(categoryId)
                .setAmount(getAmount())
                .setComments(comments));
        } else {
            return entryItems;
        }
    }

    public void setEntryItems(List<EntryItem> entryItems) {
        if (entryItems.size() == 1) {
            var entryItem = entryItems.get(0);
            entryItems.clear();
            categoryId = entryItem.getCategoryId();
            comments = entryItem.getComments();
            setAmount(entryItem.getAmount());
        } else {
            categoryId = null;
            comments = null;
            this.entryItems = entryItems;
            super.setAmount(entryItems.stream()
                .map(EntryItem::getAmount)
                .reduce(BigDecimal.ZERO,
                        BigDecimal::add));
        }
    }

    @Override
    public Movement setAmount(BigDecimal amount) {
        throw new UnsupportedOperationException("Amount should be set throw entry items.");
    }

}

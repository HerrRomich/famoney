package com.hrrm.famoney.domain.accounts.movement;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.hrrm.famoney.domain.accounts.AccountsDomainEntity;

@Entity
@Table(schema = AccountsDomainEntity.ACCOUNTS_SCHEMA_NAME, name = "entry_category")
public class EntryCategory extends AccountsDomainEntity {

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private EntryCategory parent;

    @OneToMany(mappedBy = "parent")
    private Set<EntryCategory> children;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

package com.hrrm.famoney.domain.accounts;

import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(schema = AccountBaseEntity.ACCOUNTS_SCHEMA_NAME, name = "account")
@NamedQueries({ @NamedQuery(name = Account.FIND_ALL_QUERY, query = "select a from Account a"),
        @NamedQuery(name = Account.FIND_ALL_TAGS_QUERY, query = "select distinct at from Account a join a.tags at") })
public class Account extends AccountBaseEntity {

    public static final String FIND_ALL_QUERY = "Account.findAll";
    public static final String FIND_ALL_TAGS_QUERY = "Account.findAllTags";

    @Column(name = "name")
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "tag")
    @CollectionTable(schema = AccountBaseEntity.ACCOUNTS_SCHEMA_NAME, name = "account_tag", joinColumns = {
            @JoinColumn(name = "account_id") })
    private Set<String> tags;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

}

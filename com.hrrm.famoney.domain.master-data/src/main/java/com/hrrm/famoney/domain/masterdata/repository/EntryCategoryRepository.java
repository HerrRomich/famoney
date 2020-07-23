package com.hrrm.famoney.domain.masterdata.repository;

import java.util.List;

import com.hrrm.famoney.domain.masterdata.EntryCategory;

public interface EntryCategoryRepository<T extends EntryCategory<T>> extends MasterDataDomainRepository<T> {

    List<T> getTopLevelCategories();

}

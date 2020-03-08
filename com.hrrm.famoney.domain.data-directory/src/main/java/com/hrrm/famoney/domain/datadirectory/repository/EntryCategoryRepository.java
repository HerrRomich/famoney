package com.hrrm.famoney.domain.datadirectory.repository;

import java.util.List;

import com.hrrm.famoney.domain.datadirectory.EntryCategory;

public interface EntryCategoryRepository<T extends EntryCategory<T>> extends DataDirectoryDomainRepository<T> {

    List<T> getTopLevelCategories();

}

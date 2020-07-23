package com.hrrm.famoney.application.api.masterdata.dto;

public interface EntryCategoryDTOBuilder<T extends EntryCategoryDTO<T>> {

    public EntryCategoryDTOBuilder<T> id(Integer id);

    public EntryCategoryDTOBuilder<T> name(String name);

    public EntryCategoryDTOBuilder<T> addChildren(T element);

    public EntryCategoryDTOBuilder<T> addChildren(T... elements);

    public EntryCategoryDTOBuilder<T> children(Iterable<? extends T> elements);

    public EntryCategoryDTOBuilder<T> addAllChildren(Iterable<? extends T> elements);

    T build();

}

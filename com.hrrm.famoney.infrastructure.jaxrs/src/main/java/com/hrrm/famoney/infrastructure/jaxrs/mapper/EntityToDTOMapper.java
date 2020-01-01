package com.hrrm.famoney.infrastructure.jaxrs.mapper;

import java.util.function.Supplier;

import com.hrrm.famoney.infrastructure.jaxrs.DTO;
import com.hrrm.famoney.infrastructure.persistence.DomainEntity;

public interface EntityToDTOMapper<T extends DomainEntity<?>, P extends DTO> {

    P toDTO(T entity);

    P toDTO(T entity, Supplier<P> dtoSupplier);

    T fromDTO(P dto);

    T fromDTO(P dto, Supplier<T> entitySupplier);

}

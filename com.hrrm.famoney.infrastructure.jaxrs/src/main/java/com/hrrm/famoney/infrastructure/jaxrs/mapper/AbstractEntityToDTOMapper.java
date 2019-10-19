package com.hrrm.famoney.infrastructure.jaxrs.mapper;

import com.hrrm.famoney.infrastructure.jaxrs.DTO;
import com.hrrm.infrastructure.persistence.DomainEntity;

public abstract class AbstractEntityToDTOMapper<T extends DomainEntity<?>, P extends DTO>
    implements EntityToDTOMapper<T, P> {

    @Override
    public final P toDTO(T entity) {
        return toDTO(entity, this::createEmptyDTO);
    }

    protected abstract P createEmptyDTO();

    @Override
    public final T fromDTO(P dto) {
        return fromDTO(dto, this::createEmptyEntity);
    }

    protected abstract T createEmptyEntity();

}

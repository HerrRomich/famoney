package com.hrrm.famoney.api.accounts.dto.internal;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hrrm.famoney.api.accounts.dto.MovementDTO;
import com.hrrm.famoney.api.accounts.dto.MovementSliceWithMovementsDTO;

import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@ToString
@JsonDeserialize(builder = MovementSliceWithMovementsDTOImpl.MovementSliceWithMovementsDTOImplBuilderImpl.class)
public class MovementSliceWithMovementsDTOImpl extends MovementSliceDTOImpl implements MovementSliceWithMovementsDTO {

    @Singular
    private final List<MovementDTO> movements;

    @JsonPOJOBuilder(withPrefix = "")
    public static final class MovementSliceWithMovementsDTOImplBuilderImpl extends
            MovementSliceWithMovementsDTOImplBuilder<MovementSliceWithMovementsDTOImpl, MovementSliceWithMovementsDTOImplBuilderImpl> {

    }
}

package com.hrrm.famoney.api.accounts.dto.internal;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hrrm.famoney.api.accounts.dto.MovementSliceDTO;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@ToString
@JsonDeserialize(builder = MovementSliceDTOImpl.MovementSliceDTOImplBuilderImpl.class)
public class MovementSliceDTOImpl implements MovementSliceDTO {

    private final Integer id;
    private final LocalDateTime date;
    private final Integer count;
    private final BigDecimal sum;

    @JsonPOJOBuilder(withPrefix = "")
    public static final class MovementSliceDTOImplBuilderImpl extends
            MovementSliceDTOImplBuilder<MovementSliceDTOImpl, MovementSliceDTOImplBuilderImpl> {

    }
}

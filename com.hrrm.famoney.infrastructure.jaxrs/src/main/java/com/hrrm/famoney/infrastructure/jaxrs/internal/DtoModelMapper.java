package com.hrrm.famoney.infrastructure.jaxrs.internal;

import org.modelmapper.ModelMapper;
import org.osgi.service.component.annotations.Component;

@Component(service = ModelMapper.class)
public class DtoModelMapper extends ModelMapper {

}

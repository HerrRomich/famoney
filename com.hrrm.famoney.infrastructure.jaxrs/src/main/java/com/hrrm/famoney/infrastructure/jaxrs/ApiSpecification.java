package com.hrrm.famoney.infrastructure.jaxrs;

import java.io.InputStream;

public interface ApiSpecification {

    String getPath();

    String getDescription();

    InputStream getSpecificationStream();

}

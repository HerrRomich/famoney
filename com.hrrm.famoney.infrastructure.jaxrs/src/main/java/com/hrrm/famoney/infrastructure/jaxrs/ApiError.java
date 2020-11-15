package com.hrrm.famoney.infrastructure.jaxrs;

import javax.ws.rs.core.Response.Status;

public interface ApiError {

    String getCode();

    String getMessage();

    String getPrefix();

    Status getStatus();

}

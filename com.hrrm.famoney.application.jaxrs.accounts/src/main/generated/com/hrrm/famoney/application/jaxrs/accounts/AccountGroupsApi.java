package com.hrrm.famoney.application.jaxrs.accounts;

import com.hrrm.famoney.application.jaxrs.accounts.dto.AccountGroupDTO;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import java.io.InputStream;
import java.util.Map;
import java.util.List;
import javax.validation.constraints.*;
import javax.validation.Valid;

@Path("/accountGroups")
@javax.annotation.processing.Generated(value = "com.hrrm.openapi.CustomJavaJAXRSSpecServerCodegen", date = "2018-12-17T12:25:16.590+01:00[Europe/Berlin]")
public interface AccountGroupsApi {

    @GET
    @Produces({ "application/json" })
    List<AccountGroupDTO> getAllAccountGroups();
}

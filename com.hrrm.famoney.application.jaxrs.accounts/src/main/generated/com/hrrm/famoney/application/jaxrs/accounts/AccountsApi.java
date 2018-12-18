package com.hrrm.famoney.application.jaxrs.accounts;

import com.hrrm.famoney.application.jaxrs.accounts.dto.AccountDTO;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import java.io.InputStream;
import java.util.Map;
import java.util.List;
import javax.validation.constraints.*;
import javax.validation.Valid;

@Path("/accounts")
@javax.annotation.Generated(value = "com.hrrm.openapi.CustomJavaJAXRSSpecServerCodegen", date = "2018-12-18T03:51:49.965600800+01:00[Europe/Berlin]")
public interface AccountsApi {

    @GET
    @Produces({ "application/json" })
    List<AccountDTO> getAllAccounts();
}

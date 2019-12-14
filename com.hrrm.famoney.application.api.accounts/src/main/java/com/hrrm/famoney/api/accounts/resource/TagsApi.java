package com.hrrm.famoney.api.accounts.resource;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("tags")
@Tag(name = "accounts")
public interface TagsApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponse(description = "A list of all account tags")
    List<String> getAllAccountTags();

}

package com.hrrm.famoney.api.accounts.resource;

import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.hrrm.famoney.api.accounts.dto.AccountDTO;
import com.hrrm.famoney.api.accounts.dto.AccountMovementDTO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/accounts")
@Tag(name = "accounts")
public interface AccountsApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponse(responseCode = "200", description = "A list of all accounts", content = @Content(mediaType = MediaType.APPLICATION_JSON, array = @ArraySchema(schema = @Schema(implementation = AccountDTO.class))))
    List<AccountDTO> getAllAccounts(@QueryParam("tags") Set<String> tags);

    @GET
    @Path("tags")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponse(responseCode = "200", description = "A list of all account tags", content = @Content(mediaType = MediaType.APPLICATION_JSON, array = @ArraySchema(schema = @Schema(implementation = String.class))))
    List<String> getAllAccountTags();

    @GET
    @Path("{accountId}/movements")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponse(responseCode = "200", description = "A list of account movements of specified account", content = @Content(mediaType = MediaType.APPLICATION_JSON, array = @ArraySchema(schema = @Schema(implementation = AccountMovementDTO.class))))
    List<AccountMovementDTO> getAccountMovements(
            @Parameter(name = "accountId", in = ParameterIn.PATH) @NotNull Integer accountId,
            @Parameter(name = "continue", in = ParameterIn.QUERY) @QueryParam("continue") String continueToken,
            @Parameter(name = "count", in = ParameterIn.QUERY) @QueryParam("count") Integer count);

}

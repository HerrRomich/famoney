package com.hrrm.famoney.api.accounts.resource;

import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.hrrm.famoney.api.accounts.dto.AccountDTO;
import com.hrrm.famoney.api.accounts.dto.MovementDTO;
import com.hrrm.famoney.api.accounts.dto.MovementSliceDTO;
import com.hrrm.famoney.api.accounts.dto.MovementSliceWithMovementsDTO;
import com.hrrm.famoney.infrastructure.jaxrs.ApiErrorDTO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/accounts")
@Tag(name = "accounts")
public interface AccountsApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponse(description = "A list of all accounts")
    List<AccountDTO> getAllAccounts(
            @Parameter(name = "tags", description = "List of tags to filter accounts. If empty, all accounts will be provided") @QueryParam("tags") Set<String> tags);

    @GET
    @Path("tags")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponse(description = "A list of all account tags")
    List<String> getAllAccountTags();

    @GET
    @Path("{accountId}/movement-slices")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponse(description = "A list of slices of account movements of specified account")
    @ApiResponse(responseCode = "404", description = "No account was found for specified id.", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiErrorDTO.class)))
    List<MovementSliceDTO> getMovementSlicesByAccountId(
            @Parameter(name = "accountId", in = ParameterIn.PATH) @NotNull @PathParam("accountId") Integer accountId);

    @GET
    @Path("{accountId}/movements")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponse(description = "A list of account movements of specified account")
    @ApiResponse(responseCode = "404", description = "No account was found for specified id.", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiErrorDTO.class)))
    List<MovementDTO> getMovements(
            @Parameter(name = "accountId", in = ParameterIn.PATH, description = "Identifier of account, for which the movements will be searched.") @NotNull @PathParam("accountId") Integer accountId);

    @GET
    @Path("{accountId}/movement-slices/{sliceId}/movements")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponse(description = "A list of movements by a specified slice ")
    @ApiResponse(responseCode = "404", description = "No account was found for specified id.", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiErrorDTO.class)))
    @ApiResponse(responseCode = "404", description = "No slice was found in a specified account for specified id.", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiErrorDTO.class)))
    MovementSliceWithMovementsDTO getMovementsBySliceId(
            @Parameter(name = "accountId", in = ParameterIn.PATH) @NotNull @PathParam("accountId") Integer accountId,
            @Parameter(name = "sliceId", in = ParameterIn.PATH) @NotNull @PathParam("sliceId") Integer sliceId);

}

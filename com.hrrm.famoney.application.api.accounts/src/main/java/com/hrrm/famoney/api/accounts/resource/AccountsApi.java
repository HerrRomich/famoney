package com.hrrm.famoney.api.accounts.resource;

import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.hrrm.famoney.api.accounts.dto.AccountDTO;
import com.hrrm.famoney.api.accounts.dto.AccountDataDTO;
import com.hrrm.famoney.api.accounts.dto.MovementDTO;
import com.hrrm.famoney.api.accounts.dto.MovementDataDTO;
import com.hrrm.famoney.infrastructure.jaxrs.ApiErrorDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("accounts")
@Tag(name = "accounts")
public interface AccountsApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Gets a list of accounts filtered by tags.")
    @ApiResponse(description = "A list of all accounts")
    List<AccountDTO> getAllAccounts(@Parameter(name = "tags",
            description = "List of tags to filter accounts. If empty, all accounts will be provided") @QueryParam("tags") Set<String> tags);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Adds a new account.")
    void addAccount(AccountDataDTO accountData);

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Changes a specified account.")
    @ApiResponse(description = "A changed account.")
    @ApiResponse(responseCode = "404", description = "No account was found for specified id.", content = @Content(
            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiErrorDTO.class)))
    AccountDataDTO changeAccount(@PathParam("id") Integer id, AccountDataDTO accountData);

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Gets a detailed account info.")
    @ApiResponse(description = "A detailed account info.")
    @ApiResponse(responseCode = "404", description = "No account was found for specified id.", content = @Content(
            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiErrorDTO.class)))
    AccountDTO getAccount(@PathParam("id") Integer id);

    @GET
    @Path("{id}/movements")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Gets a sorted list of account movements.")
    @ApiResponse(description = "A list of account movements of specified account.")
    @ApiResponse(responseCode = "404", description = "No account was found for specified id.", content = @Content(
            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiErrorDTO.class)))
    List<MovementDTO> getMovements(@Parameter(name = "id", in = ParameterIn.PATH,
            description = "Identifier of account, for which the movements will be searched.") @NotNull @PathParam("id") Integer id,
            @Parameter(name = "offset", in = ParameterIn.QUERY,
                    description = "Offset in the ordered list of movements. If omited, then from first movement.") @QueryParam("offset") Integer offset,
            @Parameter(name = "limit", in = ParameterIn.QUERY,
                    description = "Count of movements starting from offset. If omitted, then all from offset.") @QueryParam("limit") Integer limit);

    @POST
    @Path("{id}/movements")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Creates a a new account movement.")
    @ApiResponse(description = "New account movement will be created.")
    @ApiResponse(responseCode = "404", description = "No account was found for specified id.", content = @Content(
            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiErrorDTO.class)))
    MovementDTO addMovement(@Parameter(name = "id", in = ParameterIn.PATH,
            description = "Identifier of account, for which the movements will be searched.") @NotNull @PathParam("id") Integer id,
            MovementDataDTO movementDataDTO);

    @GET
    @Path("{id}/movements/{movementId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Gets a movement of account, specified by id.")
    @ApiResponse(description = "A Movement of account specified by id.")
    @ApiResponse(responseCode = "404", description = "No account was found for specified id.", content = @Content(
            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiErrorDTO.class)))
    @ApiResponse(responseCode = "404", description = "No movement was found in an account for specified id.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(
                    implementation = ApiErrorDTO.class)))
    MovementDTO getMovement(@Parameter(name = "id", in = ParameterIn.PATH,
            description = "Identifier of account, for which the movement will be searched.") @NotNull @PathParam("id") Integer id,
            @Parameter(name = "movementId", in = ParameterIn.PATH,
                    description = "Identifier of movement that will be searched.") @NotNull @PathParam("movementId") Integer movementId);

}

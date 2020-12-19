package com.hrrm.famoney.api.accounts.resource;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

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

@Path("accounts/{accountId}/movements")
@Tag(name = "accounts")
public interface AccountMovementsApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Gets a sorted list of account movements.")
    @ApiResponse(description = "A list of account movements of specified account.")
    @ApiResponse(
            responseCode = "404",
            description = "No account was found for specified id.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ApiErrorDTO.class)))
    List<MovementDTO> getMovements(@Parameter(
            name = "accountId",
            in = ParameterIn.PATH,
            description = "Identifier of account, for which the movements will be searched.") @NotNull @PathParam("accountId") Integer accountId,
        @Parameter(
                name = "offset",
                in = ParameterIn.QUERY,
                description = "Offset in the ordered list of movements. If omited, then from first movement.") @QueryParam("offset") Integer offset,
        @Parameter(
                name = "limit",
                in = ParameterIn.QUERY,
                description = "Count of movements starting from offset. If omitted, then all from offset.") @QueryParam("limit") Integer limit);

    @GET
    @Path("{movementId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Gets a movement of account, specified by id.")
    @ApiResponse(description = "A movement of account specified by id is returned.")
    @ApiResponse(
            responseCode = "404",
            description = "No account was found for specified id.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ApiErrorDTO.class)))
    @ApiResponse(
            responseCode = "404",
            description = "No movement was found in an account for specified id.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ApiErrorDTO.class)))
    MovementDTO getMovement(@Parameter(
            name = "accountId",
            in = ParameterIn.PATH,
            description = "Identifier of account, for which the movement will be searched.") @NotNull @PathParam("accountId") Integer accountId,
        @Parameter(
                name = "movementId",
                in = ParameterIn.PATH,
                description = "Identifier of movement that will be searched.") @NotNull @PathParam("movementId") Integer movementId);

    @PUT
    @Path("{movementId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Changes an account movement.")
    @ApiResponse(
            responseCode = "201",
            description = "An existed account movement is changed.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = MovementDTO.class)))
    @ApiResponse(
            responseCode = "404",
            description = "No account was found for specified id.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ApiErrorDTO.class)))
    @ApiResponse(
            responseCode = "404",
            description = "No account movement was found for specified id.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ApiErrorDTO.class)))
    MovementDTO changeMovement(@Parameter(
            name = "accountId",
            in = ParameterIn.PATH,
            description = "Identifier of account, for which the movements will be searched.") @NotNull @PathParam("accountId") Integer accountId,
        @Parameter(
                name = "movementId",
                in = ParameterIn.PATH,
                description = "Identifier of movement that will be searched.") @NotNull @PathParam("movementId") Integer movementId,
        MovementDataDTO movementDataDTO);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Creates a new account movement.")
    @ApiResponse(description = "New account movement is created.")
    @ApiResponse(
            responseCode = "404",
            description = "No account was found for specified id.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ApiErrorDTO.class)))
    MovementDTO addMovement(@Parameter(
            name = "accountId",
            in = ParameterIn.PATH,
            description = "Identifier of account, for which the movements will be searched.") @NotNull @PathParam("accountId") Integer accountId,
        MovementDataDTO movementDataDTO);

    @DELETE
    @Path("{movementId}")
    @Operation(description = "Deletes an existing account movement.")
    @ApiResponse(responseCode = "204", description = "An account movement is deleted.")
    @ApiResponse(
            responseCode = "404",
            description = "No account movement was found for specified id.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ApiErrorDTO.class)))
    @ApiResponse(
            responseCode = "404",
            description = "No account movement was found for specified id.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ApiErrorDTO.class)))
    void deleteMovement(@Parameter(
            name = "accountId",
            in = ParameterIn.PATH,
            description = "Identifier of account, for which the movements will be searched.") @NotNull @PathParam("accountId") Integer accountId,
        @Parameter(
                name = "movementId",
                in = ParameterIn.PATH,
                description = "Identifier of movement that will be searched.") @NotNull @PathParam("movementId") Integer movementId);

}

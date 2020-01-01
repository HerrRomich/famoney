package com.hrrm.famoney.application.api.datadirectory.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.hrrm.famoney.application.api.datadirectory.dto.EntryCategoriesDTO;
import com.hrrm.famoney.application.api.datadirectory.dto.EntryCategoryDataDTO;
import com.hrrm.famoney.infrastructure.jaxrs.ApiErrorDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("entry-categories")
@Tag(name = "data-directory")
public interface EntryCategoriesApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Gets all entry categories.")
    @ApiResponse(description = "Entry categories.")
    EntryCategoriesDTO getEntryCategories();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Adds a new entry category.")
    @ApiResponse(responseCode = "404", description = "No parent entry category was found for specified id.",
        content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(
            implementation = ApiErrorDTO.class)))
    void addEntryCategory(EntryCategoryDataDTO entryCategory);

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Changes an existing entry category.")
    @ApiResponse(description = "A changed entry category.")
    @ApiResponse(responseCode = "404", description = "No entry category was found for specified id.",
        content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(
            implementation = ApiErrorDTO.class)))
    @ApiResponse(responseCode = "404", description = "No parent entry category was found for specified id.",
        content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(
            implementation = ApiErrorDTO.class)))
    EntryCategoryDataDTO changeEntryCategory(@PathParam("id") Integer id, EntryCategoryDataDTO entryCategory);

    @DELETE
    @Path("{id}")
    @ApiResponse(responseCode = "404", description = "No entry category was found for specified id.",
        content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(
            implementation = ApiErrorDTO.class)))
    void deleteEntryCategory(@PathParam("id") Integer id);

}

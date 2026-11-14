package com.company.accessManagement.controller;

import com.company.accessManagement.dto.SampleDto;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.util.List;

@Path("/sample")
@Provider
public class SampleResource {

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Get list of access requests"
    )
    @APIResponse(
            responseCode = "200",
            description = "Request found"
    )
    public List<String> list() {
        return List.of("test1", "test2");
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Get access request"
    )
    @APIResponse(
            responseCode = "200",
            description = "Request found"
    )
    public String list(@PathParam("id") String id) {
        return id;
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Update access request"
    )
    @APIResponse(
            responseCode = "204",
            description = "Request found"
    )
    public void update(
            @PathParam("id") String id,
            SampleDto sampleDto
    ) {
        System.out.println("put request received");
        System.out.println(id);
        System.out.println(sampleDto);
    }

    @DELETE
    @Path("/{id}")
    @Operation(
            summary = "Delete access request"
    )
    @APIResponse(
            responseCode = "201",
            description = "Request found"
    )
    public void delete(
            @PathParam("id") String id
    ) {
        System.out.println("delete request received");
        System.out.println(id);
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Create access request"
    )
    @APIResponse(
            responseCode = "201",
            description = "Request found"
    )
    public void create(
            SampleDto sampleDto
    ) {
        System.out.println("post request received");
        System.out.println(sampleDto.toString());
    }

}

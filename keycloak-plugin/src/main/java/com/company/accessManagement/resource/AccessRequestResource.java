package com.company.accessManagement.resource;

import com.company.accessManagement.domain.entity.AccessRequest;
import com.company.accessManagement.dto.CreateAccessRequestDto;
import com.company.accessManagement.mapper.AccessRequestMapper;
import com.company.accessManagement.security.SecurityService;
import com.company.accessManagement.service.AccessRequestService;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;

public class AccessRequestResource {
    private final KeycloakSession session;
    private final AccessRequestService service;
    private final SecurityService security;

    public AccessRequestResource(KeycloakSession session, AccessRequestService service, SecurityService security) {
        this.session = session;
        this.service = service;
        this.security = security;
    }

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(CreateAccessRequestDto dto) {
        try {
            EntityManager em = session.getProvider(JpaConnectionProvider.class).getEntityManager();
            UserModel user = security.getCurrentUser();
            AccessRequest entity = service.create(user, dto, em);

            return Response.ok(
                    AccessRequestMapper.toDto(entity)
            ).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/access-requests/my")
    @Produces(MediaType.APPLICATION_JSON)
    public Response myRequests() {
        EntityManager em = session.getProvider(JpaConnectionProvider.class).getEntityManager();
        UserModel user = security.getCurrentUser();

        return Response.ok(
                AccessRequestMapper.toDto(
                        service.myRequests(user)
                )
        ).build();
    }


}
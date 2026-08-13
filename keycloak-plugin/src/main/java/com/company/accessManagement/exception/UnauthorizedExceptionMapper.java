package com.company.accessManagement.exception;

import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class UnauthorizedExceptionMapper implements ExceptionMapper<NotAuthorizedException>{

    @Override
    public Response toResponse(NotAuthorizedException exception){

        return Response.status(Response.Status.UNAUTHORIZED)
                .type(MediaType.APPLICATION_JSON)
                .entity("""
                        {
                            "message":"Unauthorized"
                        }
                        """)
                .build();

    }

}
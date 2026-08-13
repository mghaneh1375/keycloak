package com.company.accessManagement.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class DuplicateRequestExceptionMapper implements ExceptionMapper<DuplicateRequestException>{

    @Override
    public Response toResponse(DuplicateRequestException exception){

        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity("""
                        {
                            "message":"Duplicate pending request"
                        }
                        """)
                .build();

    }

}
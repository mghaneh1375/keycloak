package com.company.accessManagement.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

@Path("/")
@Provider
public class AccessResource {

    private String getContentType(String file) {

        if (file.endsWith(".html"))
            return "text/html";

        if (file.endsWith(".js"))
            return "application/javascript";

        if (file.endsWith(".css"))
            return "text/css";

        if (file.endsWith(".json"))
            return "application/json";

        if (file.endsWith(".svg"))
            return "image/svg+xml";

        if (file.endsWith(".png"))
            return "image/png";

        if (file.endsWith(".ico"))
            return "image/x-icon";

        if (file.endsWith(".woff2"))
            return "font/woff2";

        if (file.endsWith(".woff"))
            return "font/woff";

        return "application/octet-stream";
    }

    private Response serveStaticFile(String file) {
        if (file == null ||
                file.contains("..") ||
                file.contains("\\") ||
                file.startsWith("/")) {

            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String resourcePath = "swagger-ui/" + file;

        InputStream stream = getClass()
                .getClassLoader()
                .getResourceAsStream(resourcePath);

        if (stream == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(stream)
                .type(getContentType(file))
                .header("X-Content-Type-Options", "nosniff")
                .build();
    }

    @GET
    @Path("/swagger-ui")
    @Produces(MediaType.TEXT_HTML)
    public Response swaggerUi() {

        return Response.seeOther(
                URI.create("swagger-ui/index.html")
        ).build();
    }

    @GET
    @Path("/swagger-ui/{file: .+}")
    public Response swaggerUiFiles(
            @PathParam("file") String file) {

        return serveStaticFile(file);
    }

    @GET
    @Path("/openapi.json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOpenApi() throws IOException {

        try (InputStream stream = getClass()
                .getClassLoader()
                .getResourceAsStream(
                        "META-INF/resources/access-openapi.json"
                )) {

            if (stream == null) {
                return Response.status(404).build();
            }

            String json = new String(
                    stream.readAllBytes(),
                    StandardCharsets.UTF_8
            );

            return Response.ok(json).build();
        }
    }
}
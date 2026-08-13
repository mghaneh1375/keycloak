package com.company.accessManagement.security;

import jakarta.ws.rs.NotAuthorizedException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;

public class SecurityService {

    private final KeycloakSession session;

    public SecurityService(KeycloakSession session) {
        this.session = session;
    }

    private AuthenticationManager.AuthResult authenticate() {
        AuthenticationManager.AuthResult auth =
                new AppAuthManager.BearerTokenAuthenticator(session)
                        .authenticate();

        if (auth == null) {
            throw new NotAuthorizedException("Bearer token is missing or invalid");
        }

        return auth;
    }

    public UserModel getCurrentUser() {
        return authenticate().getUser();
    }

}
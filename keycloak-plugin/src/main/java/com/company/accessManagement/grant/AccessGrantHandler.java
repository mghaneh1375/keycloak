package com.company.accessManagement.grant;

import com.company.accessManagement.dto.AccessGrantRequestedEvent;
import org.keycloak.models.KeycloakSession;

public interface AccessGrantHandler {

    void grant(
            KeycloakSession session,
            AccessGrantRequestedEvent accessGrantRequestedEvent
    );

    String getTargetType();
}
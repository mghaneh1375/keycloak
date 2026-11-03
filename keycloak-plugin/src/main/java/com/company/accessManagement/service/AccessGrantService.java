package com.company.accessManagement.service;

import com.company.accessManagement.dto.AccessGrantRequestedEvent;
import com.company.accessManagement.grant.AccessGrantHandler;
import org.keycloak.models.KeycloakSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccessGrantService {

    private final Map<String, AccessGrantHandler> handlers = new HashMap<>();

    public AccessGrantService(List<AccessGrantHandler> handlers) {
        for (AccessGrantHandler handler : handlers) {
            this.handlers.put(
                    handler.getTargetType(),
                    handler
            );
        }
    }

    public void grant(
            KeycloakSession session,
            AccessGrantRequestedEvent event
    ) {
        AccessGrantHandler handler =
                handlers.get(event.getTargetType().name().toUpperCase());

        if (handler == null) {
            throw new IllegalArgumentException(
                    "Unsupported grant type: "
                            + event.getTargetType().name()
            );
        }

        handler.grant(session, event);
    }
}
package com.company.accessManagement.grant;

import com.company.accessManagement.dto.AccessGrantRequestedEvent;
import org.keycloak.models.KeycloakSession;

public class ClientRoleGrantHandler implements AccessGrantHandler {
    @Override
    public String getTargetType() {
        return "CLIENT_ROLE";
    }

    @Override
    public void grant(KeycloakSession session, AccessGrantRequestedEvent event) {
        var realm = session.realms().getRealmByName(event.getRealmName());
        if (realm == null) throw new IllegalArgumentException("Realm not found");
        session.getContext().setRealm(realm);
        var client = realm.getClientByClientId(event.getClientId());
        if (client == null) throw new IllegalArgumentException("Client not found: " + event.getClientId());
        var role = client.getRole(event.getTargetId());
        if (role == null) throw new IllegalArgumentException("Client role not found: " + event.getTargetId());
        var user = session.users().getUserByUsername(realm, event.getRequesterId());
        if (user == null) throw new IllegalArgumentException("Requester not found");
        if (!user.hasRole(role)) user.grantRole(role);
    }
}

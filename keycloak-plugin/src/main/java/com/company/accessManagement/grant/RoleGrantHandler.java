package com.company.accessManagement.grant;

import com.company.accessManagement.dto.AccessGrantRequestedEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;


public class RoleGrantHandler implements AccessGrantHandler {

    @Override
    public String getTargetType() {
        return "REALM_ROLE";
    }

    @Override
    public void grant(
            KeycloakSession session,
            AccessGrantRequestedEvent accessGrantRequestedEvent
    ) {
        System.out.println("In RoleGrantHandler");
        RealmModel realm =
                session.realms()
                        .getRealmByName(accessGrantRequestedEvent.getRealmName());

        if (realm == null) {
            throw new IllegalStateException(
                    "Realm not found: " + accessGrantRequestedEvent.getRealmName()
            );
        }
        System.out.println("Ream is ok");

        session.getContext().setRealm(realm);
        UserModel user =
                session.users()
                        .getUserByUsername(
                                realm,
                                accessGrantRequestedEvent.getRequesterId()
                        );

        if (user == null) {
            throw new IllegalArgumentException(
                    "User not found: " + accessGrantRequestedEvent.getRequesterId()
            );
        }
        System.out.println("User is ok");

        RoleModel role =
                realm.getRole(accessGrantRequestedEvent.getTargetId());

        if (role == null) {
            throw new IllegalArgumentException(
                    "Role not found: " + accessGrantRequestedEvent.getTargetId()
            );
        }
        System.out.println("Role is ok");

        if (!user.hasRole(role)) {
            System.out.println("Grant role");
            user.grantRole(role);
        }
    }
}
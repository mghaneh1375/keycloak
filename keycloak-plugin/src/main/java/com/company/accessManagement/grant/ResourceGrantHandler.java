package com.company.accessManagement.grant;

import com.company.accessManagement.dto.AccessGrantRequestedEvent;
import org.keycloak.authorization.AuthorizationProvider;
import org.keycloak.authorization.model.Policy;
import org.keycloak.authorization.model.Resource;
import org.keycloak.authorization.model.ResourceServer;
import org.keycloak.authorization.store.PolicyStore;
import org.keycloak.authorization.store.ResourceStore;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.representations.idm.authorization.UserPolicyRepresentation;

import java.util.HashSet;
import java.util.Set;

public class ResourceGrantHandler implements AccessGrantHandler {

    @Override
    public String getTargetType() {
        return "RESOURCE";
    }

    @Override
    public void grant(
            KeycloakSession session,
            AccessGrantRequestedEvent event
    ) {

        RealmModel realm =
                session.realms()
                        .getRealmByName(event.getRealmName());

        if (realm == null) {
            throw new IllegalStateException(
                    "Realm not found: " + event.getRealmName()
            );
        }
        session.getContext().setRealm(realm);

        AuthorizationProvider authorization =
                session.getProvider(
                        AuthorizationProvider.class
                );

        if (authorization == null) {
            throw new IllegalStateException(
                    "AuthorizationProvider not available"
            );
        }

        ResourceServer resourceServer =
                authorization
                        .getStoreFactory()
                        .getResourceServerStore()
                        .findById(
                                event.getResourceServerId()
                        );

        if (resourceServer == null) {
            throw new IllegalArgumentException(
                    "Resource server not found: "
                            + event.getResourceServerId()
            );
        }

        ResourceStore resourceStore =
                authorization
                        .getStoreFactory()
                        .getResourceStore();

        Resource resource =
                resourceStore.findById(
                        resourceServer,
                        event.getTargetId()
                );

        if (resource == null) {
            throw new IllegalArgumentException(
                    "Resource not found: "
                            + event.getTargetId()
            );
        }

        PolicyStore policyStore =
                authorization
                        .getStoreFactory()
                        .getPolicyStore();

        String policyName =
                "access-grant-" +
                        event.getRequestId();

        Policy existingPolicy =
                policyStore.findByName(
                        resourceServer,
                        policyName
                );

        if (existingPolicy != null) {
            System.out.println(
                    "Policy already exists: "
                            + policyName
            );

            return;
        }

        UserPolicyRepresentation policyRepresentation =
                new UserPolicyRepresentation();

        policyRepresentation.setName(policyName);
        policyRepresentation.setDescription(
                "Access grant for request "
                        + event.getRequestId()
        );

        Set<String> tmp = new HashSet<>();
        tmp.add(event.getRequesterId());
        policyRepresentation.setUsers(tmp);

        Policy policy =
                policyStore.create(
                        resourceServer,
                        policyRepresentation
                );

        policy.addResource(resource);
        System.out.println(
                "Resource grant completed. " +
                        "resource=" + resource.getName() +
                        ", user=" + event.getRequesterId() +
                        ", policy=" + policy.getName()
        );
    }
}
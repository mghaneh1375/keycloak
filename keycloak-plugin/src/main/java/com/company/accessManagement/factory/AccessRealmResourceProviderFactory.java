package com.company.accessManagement.factory;

import com.company.accessManagement.grant.*;
import com.company.accessManagement.messaging.AccessGrantConsumer;
import com.company.accessManagement.provider.AccessResourceProvider;
import com.company.accessManagement.service.AccessGrantService;
import org.keycloak.Config;
import org.keycloak.models.*;
import org.keycloak.services.resource.*;

import java.util.List;

/**
 * Retains the SPI lifecycle solely for grant consumption; intake endpoints are retired.
 */
public class AccessRealmResourceProviderFactory implements RealmResourceProviderFactory {
    private AccessGrantConsumer consumer;

    @Override
    public String getId() {
        return "access";
    }

    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        return new AccessResourceProvider();
    }

    @Override
    public void init(Config.Scope config) {
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        consumer = new AccessGrantConsumer(new AccessGrantService(List.of(new RoleGrantHandler(), new ClientRoleGrantHandler(), new ResourceGrantHandler())), factory);
        try {
            consumer.start();
        } catch (Exception e) {
            consumer.close();
            throw new IllegalStateException("Cannot start grant consumer", e);
        }
    }

    @Override
    public void close() {
        if (consumer != null) consumer.close();
    }
}

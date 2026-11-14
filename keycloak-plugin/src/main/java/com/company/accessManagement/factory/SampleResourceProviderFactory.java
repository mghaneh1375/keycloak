package com.company.accessManagement.factory;

import com.company.accessManagement.provider.SampleResourceProvider;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

/**
 * Retains the SPI lifecycle solely for grant consumption; intake endpoints are retired.
 */
public class SampleResourceProviderFactory implements RealmResourceProviderFactory {

    @Override
    public String getId() {
        return "sample";
    }

    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        return new SampleResourceProvider();
    }

    @Override
    public void init(Config.Scope config) {
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    @Override
    public void close() {

    }
}

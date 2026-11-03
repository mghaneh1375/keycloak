package com.company.accessManagement.jpa;

import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;

import java.util.List;

public class AccessJpaEntityProvider implements JpaEntityProvider {

    @Override
    public List<Class<?>> getEntities() {
        System.out.println("Loading Access JPA Entities");
        return List.of();
    }

    @Override
    public String getChangelogLocation() {
        return null;
    }

    @Override
    public String getFactoryId() {
        return AccessJpaEntityProviderFactory.ID;
    }

    @Override
    public void close() {
    }
}
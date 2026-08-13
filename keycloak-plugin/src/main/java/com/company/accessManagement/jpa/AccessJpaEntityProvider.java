package com.company.accessManagement.jpa;

import com.company.accessManagement.domain.entity.AccessRequest;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;

import java.util.Collections;
import java.util.List;

public class AccessJpaEntityProvider implements JpaEntityProvider {

    @Override
    public List<Class<?>> getEntities() {
        System.out.println("Loading Access JPA Entities");
        return Collections.singletonList(AccessRequest.class);
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
package com.company.accessManagement.provider;

import com.company.accessManagement.controller.AccessResource;
import org.keycloak.services.resource.RealmResourceProvider;

public class AccessResourceProvider implements RealmResourceProvider {

    @Override
    public Object getResource() {
        return new AccessResource();
    }

    @Override
    public void close() {
    }

}
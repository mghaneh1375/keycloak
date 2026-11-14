package com.company.accessManagement.provider;

import com.company.accessManagement.controller.SampleResource;
import org.keycloak.services.resource.RealmResourceProvider;

public class SampleResourceProvider implements RealmResourceProvider {

    @Override
    public Object getResource() {
        return new SampleResource();
    }

    @Override
    public void close() {
    }

}
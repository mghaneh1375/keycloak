package com.company.accessManagement.provider;

import com.company.accessManagement.grant.ResourceGrantHandler;
import com.company.accessManagement.grant.RoleGrantHandler;
import com.company.accessManagement.messaging.AccessGrantConsumer;
import com.company.accessManagement.service.AccessGrantService;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

import java.util.List;

public class AccessRealmResourceProviderFactory implements RealmResourceProviderFactory {

    public static final String ID = "access";
    private AccessGrantConsumer consumer;

    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        return new AccessRealmResourceProvider(session);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void init(Config.Scope config) {
        try {
            System.out.println("Here in AccessRealmResourceProviderFactory");
            consumer =
                    new AccessGrantConsumer(
                            new AccessGrantService(
                                    List.of(
                                            new RoleGrantHandler(),
                                            new ResourceGrantHandler()
                                    )
                            ),
                            "rabbitmq",
                            5672,
                            "admin",
                            "admin"
                    );

            consumer.start();
        } catch (Exception e) {
            System.out.println("Catch exception: " + e.getMessage());
            throw new RuntimeException(
                    "Could not start RabbitMQ consumer",
                    e
            );
        }
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        consumer.setSessionFactory(factory);
    }

    @Override
    public void close() {
    }

}
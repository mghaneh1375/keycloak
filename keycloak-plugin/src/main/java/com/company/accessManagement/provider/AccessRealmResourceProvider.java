package com.company.accessManagement.provider;

import com.company.accessManagement.repository.AccessRequestRepository;
import com.company.accessManagement.resource.AccessRequestResource;
import com.company.accessManagement.security.SecurityService;
import com.company.accessManagement.service.AccessRequestService;
import jakarta.persistence.EntityManager;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

public class AccessRealmResourceProvider implements RealmResourceProvider{

    private final KeycloakSession session;

    public AccessRealmResourceProvider(KeycloakSession session){
        this.session=session;
    }

    @Override
    public Object getResource(){
        EntityManager em = session.getProvider(JpaConnectionProvider.class).getEntityManager();
        AccessRequestRepository repository = new AccessRequestRepository(em, session);
        AccessRequestService service = new AccessRequestService(repository);
        SecurityService security = new SecurityService(session);

        return new AccessRequestResource(session, service, security);
    }

    @Override
    public void close(){}

}
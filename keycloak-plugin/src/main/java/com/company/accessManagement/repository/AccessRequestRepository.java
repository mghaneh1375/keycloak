package com.company.accessManagement.repository;

import com.company.accessManagement.domain.entity.AccessRequest;
import com.company.accessManagement.enums.AccessRequestStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.keycloak.models.KeycloakSession;

import java.util.List;
import java.util.Optional;

public class AccessRequestRepository {

    private final EntityManager em;

    public AccessRequestRepository(EntityManager em, KeycloakSession session) {
        this.em = em;

        try {
            FlywayMigrator.migrate(session);
        } catch (Exception e) {
            System.err.println("Migration failed but continuing: " + e.getMessage());
        }
    }

    public AccessRequest save(AccessRequest entity) {
        em.persist(entity);
        return entity;
    }

    public Optional<AccessRequest> findPending(String requesterId, String targetId) {

        TypedQuery<AccessRequest> query = em.createQuery("""
                select a
                from AccessRequest a
                where a.requesterId=:requesterId
                and a.targetId=:targetId
                and a.status=:status
                """, AccessRequest.class);

        query.setParameter("requesterId", requesterId);
        query.setParameter("targetId", targetId);
        query.setParameter("status", AccessRequestStatus.PENDING);

        List<AccessRequest> result = query.getResultList();

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(result.getFirst());
    }

    public List<AccessRequest> findMyRequests(String requesterId) {
        return em.createQuery("""
                        select a
                        from AccessRequest a
                        where a.requesterId=:requesterId
                        order by a.createdAt desc
                        """, AccessRequest.class)
                .setParameter("requesterId", requesterId)
                .getResultList();
    }
}
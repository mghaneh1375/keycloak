package com.company.accessManagement.repository;

import com.company.accessManagement.domain.entity.OutboxEvent;
import com.company.accessManagement.enums.OutboxStatus;
import jakarta.persistence.EntityManager;

import java.util.List;

public class OutboxRepository {

    private final EntityManager em;

    public OutboxRepository(EntityManager em) {
        this.em = em;
    }

    public void save(OutboxEvent event) {
        em.persist(event);
    }

    public List<OutboxEvent> findNewEvents(int limit) {
        return em.createQuery("""
                        select o
                        from OutboxEvent o
                        where o.status=:status
                        order by o.createdAt
                        """, OutboxEvent.class)
                .setParameter("status", OutboxStatus.NEW)
                .setMaxResults(limit)
                .getResultList();
    }

    public void update(OutboxEvent event) {
        em.merge(event);
    }

}
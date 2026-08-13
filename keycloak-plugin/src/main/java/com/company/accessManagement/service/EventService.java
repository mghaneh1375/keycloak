package com.company.accessManagement.service;

import com.company.accessManagement.domain.entity.OutboxEvent;
import com.company.accessManagement.dto.AccessRequestCreatedEvent;
import com.company.accessManagement.enums.OutboxStatus;
import com.company.accessManagement.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EventService {

    private final ObjectMapper mapper = new ObjectMapper();

    private final OutboxRepository repository;

    public EventService(OutboxRepository repository) {
        this.repository = repository;
    }

    public void publishAccessRequestCreated(AccessRequestCreatedEvent event) {
        try {
            String payload = mapper.writeValueAsString(event);
            repository.save(
                    OutboxEvent.builder()
                            .eventType("ACCESS_REQUEST_CREATED")
                            .payload(payload)
                            .status(OutboxStatus.NEW)
                            .retryCount(0)
                            .build()

            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
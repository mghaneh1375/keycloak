package com.company.accessManagement.service;

import com.company.accessManagement.domain.entity.AccessRequest;
import com.company.accessManagement.dto.AccessRequestCreatedEvent;
import com.company.accessManagement.dto.CreateAccessRequestDto;
import com.company.accessManagement.enums.AccessRequestStatus;
import com.company.accessManagement.exception.DuplicateRequestException;
import com.company.accessManagement.repository.AccessRequestRepository;
import com.company.accessManagement.repository.OutboxRepository;
import jakarta.persistence.EntityManager;
import org.keycloak.models.UserModel;

import java.util.List;
import java.util.UUID;

public class AccessRequestService {

    private final AccessRequestRepository repository;

    public AccessRequestService(AccessRequestRepository repository) {
        this.repository = repository;
    }

    public AccessRequest create(UserModel user, CreateAccessRequestDto dto, EntityManager em) {

        repository.findPending(user.getId(), dto.getTargetId())
                .ifPresent(a -> {
                    throw new DuplicateRequestException();
                });

        AccessRequest entity = AccessRequest.builder()
                .requesterId(user.getId())
                .requesterUsername(user.getUsername())
                .targetId(dto.getTargetId())
                .targetType(dto.getTargetType())
                .realmName(dto.getRealmName())
                .resourceServerId(dto.getResourceServerId())
                .reason(dto.getReason())
                .status(AccessRequestStatus.PENDING)
                .build();

        AccessRequest request = repository.save(entity);

        OutboxRepository outboxRepository =  new OutboxRepository(em);
        EventService eventService = new EventService(outboxRepository);

        eventService.publishAccessRequestCreated(
                AccessRequestCreatedEvent.builder()
                        .eventId(UUID.randomUUID())
                        .requestId(request.getId())
                        .requesterId(request.getRequesterId())
                        .targetType(request.getTargetType().name())
                        .targetId(request.getTargetId())
                        .realmName(request.getRealmName())
                        .resourceServerId(request.getResourceServerId())
                        .build()
        );

        return request;
    }

    public List<AccessRequest> myRequests(UserModel user) {
        return repository.findMyRequests(user.getId());
    }


}
package com.company.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccessRequestCreatedEvent {
    private UUID eventId;
    private Instant occurredAt;
    private Long requestId;
    private String requesterId;
    private String targetType;
    private String targetId;
    private String realmName;
    private String resourceServerId;
}
package com.company.accessManagement.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessRequestCreatedEvent{
    private UUID eventId;
    private Long requestId;
    private String requesterId;
    private String targetType;
    private String targetId;
    private String realmName;
    private String resourceServerId;
}
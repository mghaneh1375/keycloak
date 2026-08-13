package com.company.accessManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessGrantRequestedEvent {
    private Long requestId;
    private String requesterId;
    private String requesterUsername;
    private String targetType;
    private String targetId;
    private String realmName;
    private String resourceServerId;
}
package com.company.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
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
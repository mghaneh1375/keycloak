package com.company.accessManagement.dto;

import com.company.accessManagement.enums.AccessTargetType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessGrantRequestedEvent {
    private String requestId;
    private String clientId;
    private String requesterId;
    private String requesterUsername;
    private AccessTargetType targetType;
    private String targetId;
    private String realmName;
    private String resourceServerId;
}
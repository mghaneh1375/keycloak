package com.company.accessManagement.dto;

import com.company.accessManagement.enums.AccessRequestStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccessRequestResponseDto {
    private Long id;
    private AccessRequestStatus status;
    private String requesterUsername;
    private String targetId;
    private String message;
    private String realmName;
    private String resourceServerId;
}
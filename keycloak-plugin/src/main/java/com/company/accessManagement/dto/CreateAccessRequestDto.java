package com.company.accessManagement.dto;

import com.company.accessManagement.enums.AccessTargetType;
import lombok.Data;

@Data
public class CreateAccessRequestDto{
    private AccessTargetType targetType;
    private String targetId;
    private String reason;
    private String realmName;
    private String resourceServerId;
}
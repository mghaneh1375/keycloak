package com.company.accessManagement.mapper;

import com.company.accessManagement.domain.entity.AccessRequest;
import com.company.accessManagement.dto.AccessRequestResponseDto;

import java.util.List;

public class AccessRequestMapper {

    private AccessRequestMapper(){}

    public static AccessRequestResponseDto toDto(AccessRequest entity){
        return AccessRequestResponseDto.builder()
                .id(entity.getId())
                .status(entity.getStatus())
                .requesterUsername(entity.getRequesterUsername())
                .targetId(entity.getTargetId())
                .realmName(entity.getRealmName())
                .resourceServerId(entity.getResourceServerId())
                .message("Request submitted successfully")
                .build();
    }

    public static List<AccessRequestResponseDto> toDto(List<AccessRequest> entities){
        return entities.stream()
                .map(AccessRequestMapper::toDto)
                .toList();
    }
}
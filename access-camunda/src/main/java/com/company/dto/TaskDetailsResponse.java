package com.company.dto;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskDetailsResponse {
    private Long taskId;
    private Long requestId;
    private Long processInstanceKey;
    private Long jobKey;
    private String taskName;
    private String status;
    private Instant createdAt;
}
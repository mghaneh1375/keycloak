package com.company.dto;

import com.company.enums.WorkflowTaskStatus;
import lombok.*;

import java.time.Instant;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private Long taskId;
    private Long requestId;
    private String taskName;
    private String status;
    private Instant createdAt;
}
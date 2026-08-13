package com.company.entity;

import com.company.enums.WorkflowTaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "workflow_task")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_instance_id")
    private WorkflowInstance workflowInstance;

    @Column(name = "job_key")
    private Long jobKey;

    @Column(name = "task_name")
    private String name;

    @Enumerated(EnumType.STRING)
    private WorkflowTaskStatus status;

    private String assignee;

    @Column(name = "candidate_group")
    private String candidateGroup;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "completed_at")
    private Instant completedAt;
}
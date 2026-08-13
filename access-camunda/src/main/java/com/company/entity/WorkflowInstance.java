package com.company.entity;

import com.company.enums.WorkflowStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "workflow_instance")
@Getter
@Setter
@NoArgsConstructor
public class WorkflowInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "process_instance_key")
    private Long processInstanceKey;

    @Column(name = "process_definition_id")
    private String processDefinitionId;

    @Column(name = "business_key")
    private String businessKey;

    @Column(name = "request_id")
    private Long requestId;

    @Enumerated(EnumType.STRING)
    private WorkflowStatus status;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "finished_at")
    private Instant finishedAt;
}
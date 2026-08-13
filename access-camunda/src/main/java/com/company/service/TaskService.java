package com.company.service;

import com.company.dto.DecisionRequest;
import com.company.dto.TaskDetailsResponse;
import com.company.dto.TaskResponse;
import com.company.entity.WorkflowTask;
import com.company.enums.WorkflowTaskStatus;
import com.company.repository.WorkflowTaskRepository;
import io.camunda.zeebe.client.ZeebeClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final WorkflowTaskRepository workflowTaskRepository;
    private final ZeebeClient zeebeClient;

    public Page<TaskResponse> getTasks(Pageable pageable) {
        return workflowTaskRepository
                .findByStatus(
                        WorkflowTaskStatus.PENDING,
                        pageable
                )
                .map(this::map);
    }


    private TaskResponse map(WorkflowTask task) {
        return TaskResponse.builder()
                .taskId(task.getId())
                .taskName(task.getName())
                .status(task.getStatus().name())
                .createdAt(task.getCreatedAt())
                .requestId(
                        task.getWorkflowInstance()
                                .getRequestId()
                )
                .build();
    }

    public TaskDetailsResponse getTask(Long id) {
        WorkflowTask task =
                workflowTaskRepository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Task not found"
                                )
                        );


        return TaskDetailsResponse.builder()
                .taskId(task.getId())
                .jobKey(task.getJobKey())
                .taskName(task.getName())
                .status(task.getStatus().name())
                .createdAt(task.getCreatedAt())
                .requestId(
                        task.getWorkflowInstance()
                                .getRequestId()
                )
                .processInstanceKey(
                        task.getWorkflowInstance()
                                .getProcessInstanceKey()
                )
                .build();
    }


    public void approve(Long id, DecisionRequest request) {
        WorkflowTask task = getTaskEntity(id);
        task.setStatus(WorkflowTaskStatus.COMPLETED);

        task.setCompletedAt(Instant.now());
        workflowTaskRepository.save(task);
        completeTask(task.getJobKey(), true, request.getComment());
    }


    public void reject(Long id, DecisionRequest request) {
        WorkflowTask task = getTaskEntity(id);
        task.setStatus(
                WorkflowTaskStatus.CANCELED
        );
        task.setCompletedAt(Instant.now());

        workflowTaskRepository.save(task);
        completeTask(task.getJobKey(), false, request.getComment());
    }


    private WorkflowTask getTaskEntity(Long id) {
        return workflowTaskRepository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Task not found"
                        )
                );
    }

    public void completeTask(Long jobKey, boolean approved, String comment) {
        zeebeClient
                .newCompleteCommand(jobKey)
                .variables(
                        Map.of(
                                "approved", approved,
                                "comment", comment
                        )
                )
                .send()
                .join();
    }
}
package com.company.worker;

import com.company.entity.WorkflowInstance;
import com.company.entity.WorkflowTask;
import com.company.enums.WorkflowTaskStatus;
import com.company.repository.WorkflowInstanceRepository;
import com.company.repository.WorkflowTaskRepository;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ManagerApprovalWorker {
    private final WorkflowTaskRepository workflowTaskRepository;
    private final WorkflowInstanceRepository workflowInstanceRepository;

    @JobWorker(
        type = "manager-approval",
        autoComplete = false
    )
    public void handle(ActivatedJob job){
        Map<String,Object> variables =
                job.getVariablesAsMap();

        Long requestId =
                ((Number) variables.get("requestId")).longValue();

        WorkflowInstance instance =
                workflowInstanceRepository
                        .findByRequestId(requestId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "WorkflowInstance not found for requestId="
                                                + requestId
                                )
                        );

        WorkflowTask task =
                WorkflowTask.builder()
                    .workflowInstance(instance)
                    .jobKey(job.getKey())
                    .name("Manager Approval")
                    .status(WorkflowTaskStatus.PENDING)
                    .createdAt(Instant.now())
                    .build();

        workflowTaskRepository.save(task);
    }

}
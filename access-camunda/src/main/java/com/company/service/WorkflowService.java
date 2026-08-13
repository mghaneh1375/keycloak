package com.company.service;

import com.company.dto.AccessRequestCreatedEvent;
import com.company.entity.WorkflowInstance;
import com.company.enums.WorkflowStatus;
import com.company.repository.WorkflowInstanceRepository;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final ZeebeClient client;
    private final WorkflowInstanceRepository workflowInstanceRepository;

    public void start(AccessRequestCreatedEvent event) {

        WorkflowInstance instance = new WorkflowInstance();
        instance.setRequestId(event.getRequestId());
        instance.setStatus(WorkflowStatus.STARTING);
        instance.setCreatedAt(Instant.now());

        workflowInstanceRepository.saveAndFlush(instance);

        ProcessInstanceEvent process = client.newCreateInstanceCommand()
                .bpmnProcessId("access-request")
                .latestVersion()
                .variables(event)
                .send()
                .join();

        instance.setProcessInstanceKey(process.getProcessInstanceKey());
        instance.setProcessDefinitionId(process.getBpmnProcessId());
        instance.setStatus(WorkflowStatus.PENDING);

        workflowInstanceRepository.saveAndFlush(instance);
    }

}
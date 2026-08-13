package com.company.worker;

import com.company.dto.AccessGrantRequestedEvent;
import com.company.messaging.AccessGrantEventPublisher;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class GrantAccessWorker {

    private final AccessGrantEventPublisher eventPublisher;

    @JobWorker(type = "grant-access")
    public void execute(final ActivatedJob job) {
        Map<String, Object> variables = job.getVariablesAsMap();
        Long requestId = ((Number) variables.get("requestId")).longValue();
        String requesterId = (String) variables.get("requesterId");
        String targetId = (String) variables.get("targetId");
        String targetType = (String) variables.get("targetType");
        String realmName = (String) variables.get("realmName");
        String resourceServerId = (String) variables.get("resourceServerId");
        String requesterUsername = (String) job.getVariablesAsMap().get("requesterUsername");

        AccessGrantRequestedEvent event =
                AccessGrantRequestedEvent.builder()
                        .requestId(requestId)
                        .requesterId(requesterId)
                        .requesterUsername(requesterUsername)
                        .targetType(targetType)
                        .targetId(targetId)
                        .realmName(realmName)
                        .resourceServerId(resourceServerId)
                        .build();

        eventPublisher.publish(event);
    }

}
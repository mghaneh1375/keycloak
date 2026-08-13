package com.company.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RejectAccessWorker {

    @JobWorker(type = "reject-access")
    public void execute(final ActivatedJob job) {
        Map<String, Object> variables = job.getVariablesAsMap();

        Long requestId = ((Number) variables.get("requestId")).longValue();
        System.out.println("Reject Access : " + requestId);

    }

}
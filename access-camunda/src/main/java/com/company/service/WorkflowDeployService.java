package com.company.service;

import io.camunda.zeebe.client.ZeebeClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkflowDeployService{

    private final ZeebeClient client;

    @PostConstruct
    public void deploy(){
        client
                .newDeployResourceCommand()
                .addResourceFromClasspath("processes/access-request.bpmn")
                .send()
                .join();

        System.out.println("Workflow deployed");
    }

}
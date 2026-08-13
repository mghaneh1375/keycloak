package com.company.consumer;

import com.company.constants.RabbitConstants;
import com.company.dto.AccessRequestCreatedEvent;
import com.company.service.WorkflowService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccessRequestConsumer {

    private final WorkflowService workflowService;
    private final ObjectMapper mapper;

    @RabbitListener(queues = RabbitConstants.QUEUE)
    public void consume(String payload) throws Exception {
        AccessRequestCreatedEvent event =
                mapper.readValue(payload, AccessRequestCreatedEvent.class);

        workflowService.start(event);
    }

}
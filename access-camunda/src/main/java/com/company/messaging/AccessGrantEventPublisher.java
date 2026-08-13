package com.company.messaging;

import com.company.constants.RabbitConstants;
import com.company.dto.AccessGrantRequestedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class AccessGrantEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public AccessGrantEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(
            AccessGrantRequestedEvent event
    ) {
        rabbitTemplate.convertAndSend(
                RabbitConstants.EXCHANGE,
                RabbitConstants.GRANT_ROUTING_KEY,
                event
        );
    }
}
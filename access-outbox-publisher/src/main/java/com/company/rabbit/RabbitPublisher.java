package com.company.rabbit;

import com.company.constants.RabbitConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void publish(String payload){
        rabbitTemplate.convertAndSend(
                RabbitConstants.EXCHANGE,
                RabbitConstants.ROUTING_KEY,
                payload
        );
    }

}
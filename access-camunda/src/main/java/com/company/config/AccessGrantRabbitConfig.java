package com.company.config;

import com.company.constants.RabbitConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccessGrantRabbitConfig {

    public static final String EXCHANGE = "access.exchange";


    @Bean(name = "accessGrantExchange")
    public TopicExchange accessExchange() {
        return new TopicExchange(RabbitConstants.EXCHANGE, true, false);
    }

    @Bean
    public Queue accessGrantQueue() {
        return QueueBuilder
                .durable(RabbitConstants.GRANT_QUEUE)
                .build();
    }

    @Bean
    public Binding accessGrantBinding(
            Queue accessGrantQueue,
            TopicExchange accessExchange
    ) {
        return BindingBuilder
                .bind(accessGrantQueue)
                .to(accessExchange)
                .with(RabbitConstants.GRANT_ROUTING_KEY);
    }
}
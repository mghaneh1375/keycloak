package com.company.config;

import com.company.constants.RabbitConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitConfiguration {

    @Bean
    Queue accessQueue(){
        return QueueBuilder
                .durable(RabbitConstants.QUEUE)
                .build();
    }

    @Bean
    TopicExchange exchange(){
        return new TopicExchange(RabbitConstants.EXCHANGE);
    }

    @Bean
    Binding binding(){
        return BindingBuilder
                .bind(accessQueue())
                .to(exchange())
                .with(RabbitConstants.ROUTING_KEY);
    }
}
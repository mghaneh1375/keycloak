package com.company.config;

import com.company.constants.RabbitConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue accessQueue() {
        return new Queue(RabbitConstants.QUEUE, true);
    }

    @Bean
    public TopicExchange accessExchange() {
        return new TopicExchange(RabbitConstants.EXCHANGE);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(accessQueue())
                .to(accessExchange())
                .with(RabbitConstants.ROUTING_KEY);
    }

}
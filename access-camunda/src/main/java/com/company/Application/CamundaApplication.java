package com.company.Application;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableRabbit
@EnableTransactionManagement
@ComponentScan(basePackages = "com.company")
@EnableJpaRepositories(basePackages = "com.company.repository")
@EntityScan(basePackages = "com.company.entity")
public class CamundaApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                CamundaApplication.class,
                args
        );
    }

}
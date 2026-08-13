package com.company.accessManagement.messaging;

import com.company.accessManagement.dto.AccessGrantRequestedEvent;
import com.company.accessManagement.service.AccessGrantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import java.nio.charset.StandardCharsets;

public class AccessGrantConsumer {

    private static final String QUEUE = "access.grant.request";
    private KeycloakSessionFactory sessionFactory;
    private final ObjectMapper objectMapper;
    private final AccessGrantService accessGrantService;

    private final String host;
    private final int port;
    private final String username;
    private final String password;

    public AccessGrantConsumer(
            AccessGrantService accessGrantService,
            String host,
            int port,
            String username,
            String password
    ) {
        this.objectMapper = new ObjectMapper();
        this.accessGrantService = accessGrantService;

        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public void start() throws Exception {

        System.out.println("Start AccessGrantConsumer");
        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(
                QUEUE,
                true,
                false,
                false,
                null
        );

        channel.basicQos(1);

        DeliverCallback callback =
                (consumerTag, delivery) -> {
                    System.out.println("In delivery");
                    long deliveryTag =
                            delivery.getEnvelope()
                                    .getDeliveryTag();

                    System.out.println("DElivery tag is " + deliveryTag);

                    try {
                        String json =
                                new String(
                                        delivery.getBody(),
                                        StandardCharsets.UTF_8
                                );

                        AccessGrantRequestedEvent event =
                                objectMapper.readValue(
                                        json,
                                        AccessGrantRequestedEvent.class
                                );

                        System.out.println("pre handle");
                        System.out.println(json);
                        handle(event);

                        System.out.println("send basicAck");
                        channel.basicAck(
                                deliveryTag,
                                false
                        );
                    } catch (Exception e) {
                        System.out.println("Heyyy in exception");
                        e.printStackTrace();
                        channel.basicNack(
                                deliveryTag,
                                false,
                                true
                        );
                    }
                };

        channel.basicConsume(
                QUEUE,
                false,
                callback,
                consumerTag -> {
                }
        );
    }

    public void setSessionFactory(KeycloakSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private void handle(AccessGrantRequestedEvent event) {
        System.out.println("In handle");
        KeycloakSession session = null;
        try {
            session = sessionFactory.create();
            session.getTransactionManager().begin();
            accessGrantService.grant(
                    session,
                    event
            );
            session.getTransactionManager().commit();
        } catch (Exception e) {
            System.out.println("In exception " + e.getMessage());
            e.printStackTrace();
            if (session != null && session.getTransactionManager().isActive()) {
                session.getTransactionManager().rollback();
            }

//            throw e;
        } finally {
            if(session != null)
                session.close();
        }
    }
}
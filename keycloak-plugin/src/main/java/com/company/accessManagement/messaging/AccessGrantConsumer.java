package com.company.accessManagement.messaging;

import com.company.accessManagement.dto.AccessGrantRequestedEvent;
import com.company.accessManagement.service.AccessGrantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSessionFactory;

import java.nio.charset.StandardCharsets;

public class AccessGrantConsumer implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(AccessGrantConsumer.class);
    private static final String QUEUE = "access-request-approved";
    private static final String EXCHANGE = "access-management-exchange";
    private static final String ROUTING_KEY = "access-request.approved";
    private final AccessGrantService service;
    private final KeycloakSessionFactory sessions;
    private final ObjectMapper mapper = new ObjectMapper();
    private Connection connection;
    private Channel channel;

    public AccessGrantConsumer(AccessGrantService service, KeycloakSessionFactory sessions) {
        this.service = service;
        this.sessions = sessions;
    }

    public void start() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(System.getenv().getOrDefault("ACCESS_RABBIT_HOST", "localhost"));
        factory.setPort(Integer.parseInt(System.getenv().getOrDefault("ACCESS_RABBIT_PORT", "5672")));
        factory.setUsername(System.getenv().getOrDefault("ACCESS_RABBIT_USERNAME", "guest"));
        factory.setPassword(System.getenv().getOrDefault("ACCESS_RABBIT_PASSWORD", "guest"));
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE, "topic", true);
        channel.queueDeclare(QUEUE, true, false, false, null);
        channel.queueBind(QUEUE, EXCHANGE, ROUTING_KEY);
        channel.basicQos(1);
        channel.basicConsume(QUEUE, false, (tag, delivery) -> {
            long id = delivery.getEnvelope().getDeliveryTag();
            try {
                var event = mapper.readValue(
                        new String(delivery.getBody(), StandardCharsets.UTF_8),
                        AccessGrantRequestedEvent.class
                );
                handle(event);
                channel.basicAck(id, false);
            } catch (Exception e) {
                LOG.error("Access grant failed; requeuing", e);
                channel.basicNack(id, false, true);
            }
        }, tag -> {
        });
        LOG.infof("Access grant consumer started: queue=%s, exchange=%s, routingKey=%s",
                QUEUE, EXCHANGE, ROUTING_KEY);
    }

    private void handle(AccessGrantRequestedEvent event) {
        try (var session = sessions.create()) {
            session.getTransactionManager().begin();
            try {
                service.grant(session, event);
                session.getTransactionManager().commit();
            } catch (RuntimeException e) {
                if (session.getTransactionManager().isActive()) {
                    session.getTransactionManager().rollback();
                }
                throw e;
            }
        }
    }

    @Override
    public void close() {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
        } catch (Exception ignored) {
        }
        try {
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
        } catch (Exception ignored) {
        }
    }
}

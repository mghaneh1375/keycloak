package com.company.constants;

public final class RabbitConstants {

    public static final String EXCHANGE = "access.exchange";
    public static final String ROUTING_KEY = "access.created";
    public static final String QUEUE = "access.request";
    public static final String GRANT_QUEUE = "access.grant.request";
    public static final String GRANT_ROUTING_KEY = "access.grant.requested";

    private RabbitConstants() {
    }

}
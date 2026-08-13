package com.company.entity;

import com.company.enums.OutboxStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "outbox_event")
@Getter
@Setter
public class OutboxEvent {

    @Id
    private Long id;

    @Column(nullable = false, length = 100, name = "event_type")
    private String eventType;

    @Lob
    @Column(nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OutboxStatus status;

    @Column(nullable = false, name = "retry_count")
    private Integer retryCount;

    @Column(nullable = false, name = "created_at")
    private Instant createdAt;

    @Column(name = "published_at")
    private Instant publishedAt;

}
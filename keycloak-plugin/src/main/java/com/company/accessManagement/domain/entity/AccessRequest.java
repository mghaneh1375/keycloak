package com.company.accessManagement.domain.entity;

import com.company.accessManagement.enums.AccessRequestStatus;
import com.company.accessManagement.enums.AccessTargetType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "access_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 36, name = "requester_id")
    private String requesterId;

    @Column(nullable = false, length = 255, name = "requester_username")
    private String requesterUsername;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30, name = "target_type")
    private AccessTargetType targetType;

    @Column(nullable = false, length = 255, name = "target_id")
    private String targetId;

    @Column(nullable = false, length = 255, name = "realm_name")
    private String realmName;

    @Column(length = 255, name = "resource_server_id")
    private String resourceServerId;

    @Column(length = 1000)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AccessRequestStatus status;

    @Column(nullable = false, name = "created_at")
    private Instant createdAt;

    @Column(nullable = false, name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
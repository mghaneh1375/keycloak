-- V2__create_access_request_table.sql
CREATE TABLE IF NOT EXISTS access_request (
                                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                              requester_id VARCHAR(36) NOT NULL,
                                              requester_username VARCHAR(255) NOT NULL,
                                              target_type VARCHAR(30) NOT NULL,
                                              target_id VARCHAR(255) NOT NULL,
                                              realm_name VARCHAR(255) NOT NULL,
                                              resource_server_id VARCHAR(255),
                                              reason VARCHAR(1000),
                                              status VARCHAR(30) NOT NULL,
                                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                              updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                              INDEX idx_requester_id (requester_id),
                                              INDEX idx_target (target_type, target_id),
                                              INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
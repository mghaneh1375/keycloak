CREATE TABLE IF NOT EXISTS outbox_event
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_type   VARCHAR(100) NOT NULL,
    payload      LONGTEXT     NOT NULL,
    status       VARCHAR(20)  NOT NULL,
    retry_count  INT          NOT NULL,
    created_at   DATETIME     NOT NULL,
    published_at DATETIME     NULL
);

CREATE INDEX idx_outbox_status
    ON outbox_event (status);

CREATE INDEX idx_outbox_created
    ON outbox_event (created_at);
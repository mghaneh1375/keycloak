CREATE TABLE workflow_instance
(
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    process_instance_key  BIGINT,
    process_definition_id VARCHAR(100),
    business_key          VARCHAR(100),
    request_id            BIGINT      NOT NULL,
    status                VARCHAR(30) NOT NULL,
    created_at            DATETIME    NOT NULL,
    finished_at           DATETIME    NULL
);

CREATE TABLE workflow_task
(
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    workflow_instance_id BIGINT NOT NULL,
    task_definition      VARCHAR(100),
    task_name            VARCHAR(200),
    assignee             VARCHAR(100),
    candidate_group      VARCHAR(100),
    status               VARCHAR(30),
    created_at           DATETIME,
    completed_at         DATETIME,
    job_key              BIGINT NOT NULL,

    CONSTRAINT fk_workflow_task_instance
        FOREIGN KEY (workflow_instance_id)
            REFERENCES workflow_instance (id)
);

CREATE TABLE workflow_history
(
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    workflow_instance_id BIGINT NOT NULL,
    event_type           VARCHAR(100),
    actor                VARCHAR(100),
    payload              TEXT,
    created_at           DATETIME,

    CONSTRAINT fk_workflow_history_instance
        FOREIGN KEY (workflow_instance_id)
            REFERENCES workflow_instance (id)
);

CREATE INDEX idx_workflow_request
    ON workflow_instance (request_id);

CREATE INDEX idx_workflow_process
    ON workflow_instance (process_instance_key);

CREATE INDEX idx_task_status
    ON workflow_task (status);

CREATE INDEX idx_task_assignee
    ON workflow_task (assignee);

CREATE INDEX idx_history_instance
    ON workflow_history (workflow_instance_id);
--liquibase formatted sql

--changeset jcondotta:0003-create-bank-transfer-outbox-table
CREATE TABLE bank_transfer_outbox
(
    event_id        UUID                     NOT NULL,
    correlation_id  UUID                     NOT NULL,
    aggregate_id    VARCHAR(36)              NOT NULL,
    event_type      VARCHAR(100)             NOT NULL,
    payload         TEXT                     NOT NULL,
    shard           INTEGER                  NOT NULL,
    attempt_count   INTEGER                  NOT NULL DEFAULT 0,
    next_attempt_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_bank_transfer_outbox PRIMARY KEY (event_id)
);

CREATE INDEX idx_bank_transfer_outbox_shard_next_attempt
    ON bank_transfer_outbox (shard, next_attempt_at);

--rollback DROP INDEX idx_bank_transfer_outbox_shard_next_attempt;
--rollback DROP TABLE bank_transfer_outbox;

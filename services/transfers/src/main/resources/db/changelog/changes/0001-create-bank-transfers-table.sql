--liquibase formatted sql

--changeset jcondotta:0001-create-bank-transfers-table
CREATE TABLE bank_transfers
(
    id                   UUID                     NOT NULL,
    sender_account_id    UUID                     NOT NULL,
    recipient_account_id UUID                     NOT NULL,
    amount               NUMERIC(19, 2)           NOT NULL,
    currency             VARCHAR(3)               NOT NULL,
    transfer_type        VARCHAR(50)              NOT NULL,
    transfer_status      VARCHAR(50)              NOT NULL,
    reference            VARCHAR(255),
    created_at           TIMESTAMP WITH TIME ZONE NOT NULL,
    version              BIGINT                   NOT NULL,
    CONSTRAINT pk_bank_transfers PRIMARY KEY (id)
);

--rollback DROP TABLE bank_transfers;

--liquibase formatted sql

--changeset jcondotta:0004-create-ledger-account-table
CREATE TABLE ledger_account
(
    id                UUID                     NOT NULL,
    account_reference UUID                     UNIQUE,
    account_type      VARCHAR(20)              NOT NULL,
    currency          VARCHAR(3)               NOT NULL,
    status            VARCHAR(20)              NOT NULL,
    created_at        TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_ledger_account PRIMARY KEY (id)
);

--rollback DROP TABLE ledger_account;

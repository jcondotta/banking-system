--liquibase formatted sql

--changeset jcondotta:0001-create-ledger-account-table
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

--changeset jcondotta:0002-create-account-balance-table
CREATE TABLE account_balance
(
    account_id    UUID                     NOT NULL,
    booked_amount NUMERIC(19, 2)           NOT NULL DEFAULT 0,
    held_amount   NUMERIC(19, 2)           NOT NULL DEFAULT 0,
    version       BIGINT                   NOT NULL DEFAULT 0,
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_account_balance PRIMARY KEY (account_id),
    CONSTRAINT fk_account_balance_ledger_account
        FOREIGN KEY (account_id) REFERENCES ledger_account (id)
);

--rollback DROP TABLE account_balance;

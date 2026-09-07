--liquibase formatted sql

--changeset jcondotta:0005-create-account-balance-table
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
--rollback DROP INDEX idx_ledger_account_account_reference;

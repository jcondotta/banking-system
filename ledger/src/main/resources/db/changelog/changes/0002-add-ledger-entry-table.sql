--liquibase formatted sql

--changeset jcondotta:0003-create-ledger-entry-table
CREATE TABLE ledger_entry
(
    id            UUID                     NOT NULL,
    account_id    UUID                     NOT NULL,
    transfer_id   UUID                     NOT NULL,
    movement_type VARCHAR(10)              NOT NULL,
    amount        NUMERIC(19, 2)           NOT NULL,
    currency      VARCHAR(3)               NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_ledger_entry PRIMARY KEY (id),
    CONSTRAINT fk_ledger_entry_ledger_account
        FOREIGN KEY (account_id) REFERENCES ledger_account (id)
);

CREATE INDEX idx_ledger_entry_account_id ON ledger_entry (account_id);
CREATE INDEX idx_ledger_entry_transfer_id ON ledger_entry (transfer_id);

--rollback DROP INDEX idx_ledger_entry_transfer_id;
--rollback DROP INDEX idx_ledger_entry_account_id;
--rollback DROP TABLE ledger_entry;

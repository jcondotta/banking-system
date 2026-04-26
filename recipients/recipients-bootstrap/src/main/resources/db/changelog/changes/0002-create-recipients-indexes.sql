--liquibase formatted sql

--changeset jcondotta:0002-create-recipients-indexes
ALTER TABLE recipients
    ADD CONSTRAINT uq_recipient_bank_account_iban
        UNIQUE (bank_account_id, iban);

CREATE INDEX idx_recipient_bank_account
    ON recipients (bank_account_id);

--rollback DROP INDEX idx_recipient_bank_account;
--rollback ALTER TABLE recipients DROP CONSTRAINT uq_recipient_bank_account_iban;
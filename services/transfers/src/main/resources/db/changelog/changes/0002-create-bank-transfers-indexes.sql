--liquibase formatted sql

--changeset jcondotta:0002-create-bank-transfers-indexes
CREATE INDEX idx_bank_transfers_sender_account_id
    ON bank_transfers (sender_account_id);

CREATE INDEX idx_bank_transfers_recipient_account_id
    ON bank_transfers (recipient_account_id);

CREATE INDEX idx_bank_transfers_created_at
    ON bank_transfers (created_at);

--rollback DROP INDEX idx_bank_transfers_created_at;
--rollback DROP INDEX idx_bank_transfers_recipient_account_id;
--rollback DROP INDEX idx_bank_transfers_sender_account_id;

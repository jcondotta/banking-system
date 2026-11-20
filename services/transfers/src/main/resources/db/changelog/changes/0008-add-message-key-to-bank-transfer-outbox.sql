--liquibase formatted sql

--changeset jcondotta:0008-add-message-key-to-bank-transfer-outbox
ALTER TABLE bank_transfer_outbox ADD COLUMN message_key VARCHAR(255) NOT NULL DEFAULT '';

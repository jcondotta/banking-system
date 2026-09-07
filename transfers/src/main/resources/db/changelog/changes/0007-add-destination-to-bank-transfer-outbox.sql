--liquibase formatted sql

--changeset jcondotta:0007-add-destination-to-bank-transfer-outbox
ALTER TABLE bank_transfer_outbox
  ADD COLUMN destination VARCHAR(255) NOT NULL DEFAULT '';

--rollback ALTER TABLE bank_transfer_outbox DROP COLUMN destination;

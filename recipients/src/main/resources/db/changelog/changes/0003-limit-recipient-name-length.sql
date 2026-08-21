--liquibase formatted sql

--changeset jcondotta:0003-limit-recipient-name-length
ALTER TABLE recipients
    ALTER COLUMN name TYPE VARCHAR(50);

--rollback ALTER TABLE recipients ALTER COLUMN name TYPE VARCHAR(255);

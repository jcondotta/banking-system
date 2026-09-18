--liquibase formatted sql

--changeset jcondotta:0004-create-bank-accounts-table
CREATE TABLE bank_accounts
(
    id     UUID        NOT NULL,
    status VARCHAR(16) NOT NULL,
    CONSTRAINT pk_bank_accounts PRIMARY KEY (id)
);

--rollback DROP TABLE bank_accounts;

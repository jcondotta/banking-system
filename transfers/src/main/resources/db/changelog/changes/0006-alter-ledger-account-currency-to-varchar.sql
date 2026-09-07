--liquibase formatted sql

--changeset jcondotta:0006-alter-ledger-account-currency-to-varchar
ALTER TABLE ledger_account
    ALTER COLUMN currency TYPE VARCHAR(3);

--rollback ALTER TABLE ledger_account ALTER COLUMN currency TYPE CHAR(3);

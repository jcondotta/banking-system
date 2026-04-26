--liquibase formatted sql

--changeset jcondotta:0001-create-recipients-table
CREATE TABLE recipients
(
    id              UUID                     NOT NULL,
    bank_account_id UUID                     NOT NULL,
    name            VARCHAR(255)             NOT NULL,
    iban            VARCHAR(255)             NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    version         BIGINT                   NOT NULL,
    CONSTRAINT pk_recipients PRIMARY KEY (id)
);

--rollback DROP TABLE recipients;

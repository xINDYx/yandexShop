--liquibase formatted sql

--changeset indy:create-account-table
CREATE TABLE IF NOT EXISTS account (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    balance DOUBLE PRECISION NOT NULL
);

--changeset indy:init-account-data
INSERT INTO account (balance) VALUES (1000.0);
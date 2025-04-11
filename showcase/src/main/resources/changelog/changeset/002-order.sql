--liquibase formatted sql

--changeset indy:create-order-table
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    total_sum BIGINT NOT NULL
);
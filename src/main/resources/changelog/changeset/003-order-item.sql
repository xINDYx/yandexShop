--liquibase formatted sql

--changeset indy:create-order_item-table
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    count INT NOT NULL,
    price BIGINT NOT NULL,
    img_path VARCHAR(255),
    order_id BIGINT NOT NULL,
);
--liquibase formatted sql

--changeset indy:create-cart-table
CREATE TABLE IF NOT EXISTS carts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    img_path VARCHAR(255),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    count INT NOT NULL,
    price BIGINT NOT NULL
);
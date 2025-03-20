--liquibase formatted sql

--changeset i.puchkov:create-product-table
CREATE TABLE IF NOT EXISTS products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    img_path VARCHAR(255),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    count INT NOT NULL,
    price BIGINT NOT NULL
);

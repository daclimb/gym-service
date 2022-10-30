CREATE TABLE products
(
    id          INT PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    price       INT          NOT NULL,
    description TEXT         NOT NULL,
    created_at  TIMESTAMP    NOT NULL
);

CREATE SEQUENCE PRODUCTS_SEQ START WITH 1 INCREMENT BY 1;

CREATE TABLE product_images
(
    id         VARCHAR(36) PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    product_id INT,
    created_at TIMESTAMP    NOT NULL
)
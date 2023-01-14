CREATE TABLE gyms
(
    id          INT PRIMARY KEY,
    name        VARCHAR(255)  NOT NULL,
    franchise   VARCHAR(64)   NOT NULL,
    address     VARCHAR(255)  NOT NULL,
    description TEXT          NOT NULL,
    latitude    NUMERIC(8, 6) NOT NULL,
    longitude   NUMERIC(9, 6) NOT NULL,
    created_at  TIMESTAMP     NOT NULL,
    modified_at TIMESTAMP     NOT NULL
);

CREATE SEQUENCE GYMS_SEQ START WITH 1 INCREMENT BY 1;

CREATE TABLE gym_images
(
    id         VARCHAR(36) PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    gym_id     INT,
    created_at TIMESTAMP    NOT NULL
);

CREATE TABLE members
(
    id          SERIAL PRIMARY KEY,
    email       VARCHAR(127) NOT NULL UNIQUE,
    password    VARCHAR(127) NOT NULL,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    role        VARCHAR(16)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    modified_at TIMESTAMP    NOT NULL
);

CREATE TABLE franchises
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT         NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    modified_at TIMESTAMP    NOT NULL
)
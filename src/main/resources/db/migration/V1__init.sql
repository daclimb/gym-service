CREATE TABLE gyms
(
    id          INT PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    address     VARCHAR(255) NOT NULL,
    description TEXT         NOT NULL,
    role        VARCHAR(16)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL
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
    id          SERIAL primary key,
    email       VARCHAR(127) NOT NULL UNIQUE,
    password    VARCHAR(127) NOT NULL,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    created_at  TIMESTAMP    NOT NULL,
    modified_at TIMESTAMP    NOT NULL
);
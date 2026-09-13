-- Для @GeneratedValue(strategy = GenerationType.IDENTITY)
/*
create table client
(
    id   bigserial not null primary key,
    name varchar(50)
);

 */

-- 1. Address
CREATE SEQUENCE address_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE address (
                         id BIGINT NOT NULL PRIMARY KEY,
                         street VARCHAR(90)
);

-- 2. Client
CREATE SEQUENCE client_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE client (
                        id BIGINT NOT NULL PRIMARY KEY,
                        name VARCHAR(50),
                        address_id BIGINT,
                        CONSTRAINT fk_client_address FOREIGN KEY (address_id) REFERENCES address(id)
);

-- 3. Phone
CREATE SEQUENCE phone_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE phone (
                       id BIGINT NOT NULL PRIMARY KEY,
                       number VARCHAR(15),
                       client_id BIGINT NOT NULL,
                       CONSTRAINT fk_phone_client
                           FOREIGN KEY (client_id)
                               REFERENCES client(id)
                               ON DELETE CASCADE
);

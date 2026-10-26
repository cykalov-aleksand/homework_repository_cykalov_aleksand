-- таблицы
CREATE TABLE client (
                        id BIGSERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL
);

CREATE TABLE address (
                         id BIGSERIAL PRIMARY KEY,
                         street VARCHAR(255),
                         client_id BIGINT NOT NULL REFERENCES client(id) ON DELETE CASCADE
);

CREATE INDEX idx_address_client_id ON address(client_id);

CREATE TABLE phone (
                       id BIGSERIAL PRIMARY KEY,
                       number VARCHAR(50) NOT NULL,
                       position INTEGER NOT NULL,
                       client_id BIGINT NOT NULL REFERENCES client(id) ON DELETE CASCADE
);

CREATE INDEX idx_phone_client_id ON phone(client_id);

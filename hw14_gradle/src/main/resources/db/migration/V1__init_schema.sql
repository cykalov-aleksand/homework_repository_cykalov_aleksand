-- Сначала address, потом client (из-за FK), потом phone

CREATE TABLE IF NOT EXISTS address (
                                       id BIGSERIAL PRIMARY KEY,
                                       street VARCHAR(90)
    );

 CREATE TABLE IF NOT EXISTS client (
                                      id BIGSERIAL PRIMARY KEY,
                                      name VARCHAR(50),
                                      address_id BIGINT,
    CONSTRAINT fk_client_address FOREIGN KEY (address_id) REFERENCES address(id)
    );

CREATE TABLE IF NOT EXISTS phone (
                                     id BIGSERIAL PRIMARY KEY,
                                     number VARCHAR(15),
                                    client_id BIGINT NOT NULL,
    CONSTRAINT fk_phone_client FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE CASCADE
    );

-- Индексы (для findAllByClientId и производительности)
CREATE INDEX IF NOT EXISTS idx_phone_client_id ON phone(client_id);
CREATE INDEX IF NOT EXISTS idx_client_address_id ON client(address_id);


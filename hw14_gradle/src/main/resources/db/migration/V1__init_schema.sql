-- 1. Таблицы в порядке зависимостей: address → client → phone

CREATE TABLE IF NOT EXISTS address (
                                       id BIGSERIAL PRIMARY KEY,
                                       street VARCHAR(90) NOT NULL
    );

CREATE TABLE IF NOT EXISTS client (
                                      id BIGSERIAL PRIMARY KEY,
                                      name VARCHAR(50) NOT NULL,
    address_id BIGINT NOT NULL,  -- NOT NULL, потому что у клиента всегда должен быть адрес (one-to-one)
    CONSTRAINT fk_client_address FOREIGN KEY (address_id) REFERENCES address(id)
    ON DELETE CASCADE,        -- при удалении адреса удаляется и клиент (и его телефоны)
    CONSTRAINT unique_address UNIQUE (address_id)  -- один адрес — только у одного клиента
    );

CREATE TABLE IF NOT EXISTS phone (
                                     id BIGSERIAL PRIMARY KEY,
                                     number VARCHAR(30) NOT NULL,
    client_id BIGINT NOT NULL,
    CONSTRAINT fk_phone_client FOREIGN KEY (client_id) REFERENCES client(id)
    ON DELETE CASCADE  -- при удалении клиента телефоны удаляются автоматически
    );

-- 2. Индексы
CREATE INDEX IF NOT EXISTS idx_phone_client_id ON phone(client_id);
CREATE INDEX IF NOT EXISTS idx_client_address_id ON client(address_id);


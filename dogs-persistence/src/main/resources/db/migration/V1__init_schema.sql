CREATE TABLE supplier (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE
);

CREATE TABLE kennelling_characteristic (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE dog (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    breed VARCHAR(100) NOT NULL,
    badge_id VARCHAR(50),
    gender VARCHAR(20),
    birth_date DATE,
    date_acquired DATE,
    current_status VARCHAR(50),
    leaving_date DATE,
    leaving_reason VARCHAR(50),
    supplier_id BIGINT REFERENCES supplier(id),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE dog_kennelling_characteristic (
    dog_id BIGINT NOT NULL REFERENCES dog(id),
    characteristic_id BIGINT NOT NULL REFERENCES kennelling_characteristic(id),
    PRIMARY KEY (dog_id, characteristic_id)
);

CREATE INDEX idx_dog_deleted ON dog (deleted);
CREATE INDEX idx_dog_name ON dog (name);
CREATE INDEX idx_dog_breed ON dog (breed);
CREATE INDEX idx_dog_supplier_id ON dog (supplier_id);

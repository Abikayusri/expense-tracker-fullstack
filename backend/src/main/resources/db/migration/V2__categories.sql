CREATE TABLE categories (
    id             BIGSERIAL PRIMARY KEY,
    user_id        BIGINT      NOT NULL REFERENCES users (id),
    name           VARCHAR(50) NOT NULL,
    type           VARCHAR(10) NOT NULL,
    monthly_budget BIGINT      NULL
);

CREATE UNIQUE INDEX categories_user_type_name_uq ON categories (user_id, type, lower(name));

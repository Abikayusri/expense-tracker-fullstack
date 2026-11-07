CREATE TABLE transactions (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT       NOT NULL REFERENCES users (id),
    category_id BIGINT       NOT NULL REFERENCES categories (id),
    amount      BIGINT       NOT NULL CHECK (amount > 0),
    date        DATE         NOT NULL,
    note        VARCHAR(255) NULL
);

CREATE INDEX transactions_user_date_idx ON transactions (user_id, date);
CREATE INDEX transactions_category_date_idx ON transactions (category_id, date);

CREATE TABLE device_tokens (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT       NOT NULL REFERENCES users (id),
    fcm_token  VARCHAR(512) NOT NULL UNIQUE,
    platform   VARCHAR(10)  NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE budget_alerts (
    user_id     BIGINT  NOT NULL REFERENCES users (id),
    category_id BIGINT  NOT NULL REFERENCES categories (id),
    year_month  CHAR(7) NOT NULL,
    PRIMARY KEY (user_id, category_id, year_month)
);

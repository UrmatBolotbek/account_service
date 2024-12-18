CREATE TABLE balance (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    auth_balance DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
    current_balance DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMPTZ DEFAULT current_timestamp,
    updated_at TIMESTAMPTZ DEFAULT current_timestamp,
    version BIGINT NOT NULL
);

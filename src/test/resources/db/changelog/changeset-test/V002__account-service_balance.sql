CREATE TABLE balance (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    auth_balance DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
    actual_balance DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMPTZ DEFAULT current_timestamp,
    updated_at TIMESTAMPTZ DEFAULT current_timestamp,
    version BIGINT DEFAULT 0,
    account_id BIGINT NOT NULL,

    CONSTRAINT fk_balance_account FOREIGN KEY (account_id) REFERENCES account (id),
    CONSTRAINT chk_authorization_balance CHECK (auth_balance >= 0),
    CONSTRAINT chk_actual_balance CHECK (actual_balance >= 0)
);
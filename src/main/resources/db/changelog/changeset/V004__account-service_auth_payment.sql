CREATE TABLE payment(
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    source_balance_id BIGINT NOT NULL,
    target_balance_id BIGINT NOT NULL,
    amount            DECIMAL(10,2) NOT NULL,
    status            varchar(32) DEFAULT 'ACTIVE',
    category          varchar(32) DEFAULT 'OTHER',
    created_at        timestamptz DEFAULT current_timestamp,
    updated_at        timestamptz DEFAULT current_timestamp,
    version           BIGINT      DEFAULT 0,

    CONSTRAINT fk_source_balance_id FOREIGN KEY (source_balance_id) REFERENCES balance (id),
    CONSTRAINT fk_target_balance_id FOREIGN KEY (target_balance_id) REFERENCES balance (id)
);

ALTER TABLE account
    DROP COLUMN balance_id;

CREATE INDEX idx_balance_account_id ON balance(account_id);


ALTER TABLE account
    ADD COLUMN balance_id BIGINT;

ALTER TABLE account
    ADD CONSTRAINT fk_account_balance
    FOREIGN KEY (balance_id)
    REFERENCES balance (id);

ALTER TABLE account
    ALTER COLUMN version TYPE BIGINT;

DROP INDEX IF EXISTS idx_number;

CREATE INDEX idx_owner_id ON account(owner_id);
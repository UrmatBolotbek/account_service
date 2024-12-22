ALTER TABLE account
    ADD COLUMN balance_id BIGINT;

ALTER TABLE account
    ALTER COLUMN version TYPE BIGINT;
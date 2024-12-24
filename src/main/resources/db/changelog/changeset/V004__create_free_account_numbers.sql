CREATE TABLE free_account_numbers (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    type VARCHAR(20) NOT NULL,
    account_number INT
)
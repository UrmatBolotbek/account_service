CREATE TABLE account (
                             id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                             number VARCHAR(20) NOT NULL UNIQUE,
                             owner_type VARCHAR(20) NOT NULL,
                             owner_id bigint NOT NULL UNIQUE,
                             account_type VARCHAR(30) NOT NULL,
                             currency CHARACTER(3) NOT NULL,
                             status VARCHAR(30) NOT NULL,
                             created_at timestamptz DEFAULT current_timestamp,
                             updated_at timestamptz DEFAULT current_timestamp,
                             close_at TIMESTAMP,
                             version INT NOT NULL
);

CREATE INDEX idx_number on account(number)

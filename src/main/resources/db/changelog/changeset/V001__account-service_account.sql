CREATE TABLE account (
                             id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                             number bigint NOT NULL UNIQUE,
                             owner_id bigint NOT NULL UNIQUE,
                             type varchar(64) NOT NULL,
                             currency varchar(64) NOT NULL,
                             status varchar(64) NOT NULL,
                             created_at timestamptz DEFAULT current_timestamp,
                             updated_at timestamptz DEFAULT current_timestamp,
                             close_at timestamptz DEFAULT current_timestamp,


);

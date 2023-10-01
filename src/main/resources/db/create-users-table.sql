DROP TABLE users;
CREATE TABLE IF NOT EXISTS users (
    id                    uuid         PRIMARY KEY,
    first_name            varchar(30)  NOT NULL,
    last_name             varchar(30)  NOT NULL,
    email                 varchar(100) UNIQUE NOT NULL,
    password              varchar(200)  NOT NULL,
    phone_number          varchar(17)  UNIQUE NOT NULL,
    status                varchar(50)  NOT NULL DEFAULT 'ACTIVE',
    role                  varchar(50)  NOT NULL DEFAULT 'CUSTOMER',
    created_at            timestamp    NOT NULL,
    modified_at           timestamp    NOT NULL
    )
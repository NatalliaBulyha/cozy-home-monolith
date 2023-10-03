DROP TABLE users;
CREATE TABLE IF NOT EXISTS users (
    id                    uuid         PRIMARY KEY,
    first_name            varchar(30)  NOT NULL,
    last_name             varchar(30)  NOT NULL,
    email                 varchar(100) UNIQUE NOT NULL,
    password              varchar(200) NOT NULL,
    phone_number          varchar(17)  UNIQUE NOT NULL,
    status                varchar(50)  NOT NULL DEFAULT 'ACTIVE',
    role                  varchar(50)  NOT NULL DEFAULT 'CUSTOMER',
    created_at            timestamp    NOT NULL,
    modified_at           timestamp    NOT NULL
);

CREATE TABLE IF NOT EXISTS roles (
    id                    uuid         PRIMARY KEY,
    name                  varchar(50)  NOT NULL,
);

INSERT INTO roles (id, name)
VALUES
    ('11414692-0a17-454e-b4a7-ee6736fbabbb','ROLE_USER'),
    ('a534d557-6555-4af1-a4db-36abfb6c3b38', 'ROLE_ADMIN');

INSERT INTO users (username, password, email)
VALUES
    ('ecf3720c-6122-11ee-8c99-0242ac120002', 'Alina', 'Kuchure', 'user@gmail.com', '$2a$12$wCvK4/NO7iiXIndDxZB69e6DGiAfoCqKOeWE.UOQur7WbiqRLNCoe', '+38(123)123-45-67'),
    ('admin', '$2a$04$Fx/SX9.BAvtPlMyIIqqFx.hLY2Xp8nnhpzvEEVINvVpwIPbA3v/.i', 'admin@gmail.com');

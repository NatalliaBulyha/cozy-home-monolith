CREATE TABLE IF NOT EXISTS reviews (
    id                uuid         NOT NULL PRIMARY KEY,
    rating            int          CHECK ( rating > 0 AND rating < 6),
    comment           varchar(500) NOT NULL,
    created_at        timestamp    NOT NULL,
    modified_at       timestamp    NOT NULL,
    user_name         varchar(40)  NOT NULL,
    product_sku_code  varchar(6)   NOT NULL
    )
-- Merch Store, Saved Payment Methods, and Orders Migration

-- =========================
-- DROP (order matters for FK)
-- =========================
DROP TABLE IF EXISTS merch_order_items CASCADE;
DROP TABLE IF EXISTS merch_orders CASCADE;
DROP TABLE IF EXISTS saved_payment_methods CASCADE;
DROP TABLE IF EXISTS merch_products CASCADE;

-- =========================
-- MERCH PRODUCTS
-- =========================
CREATE TABLE IF NOT EXISTS merch_products
(
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(200)   NOT NULL,
    description      TEXT           NULL,
    price            NUMERIC(10, 2) NOT NULL,
    image_url        VARCHAR(500)   NULL,
    stock_quantity   INT            NOT NULL DEFAULT 0,
    category         VARCHAR(100)   NULL,
    active           BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       BIGINT         NULL,
    CONSTRAINT fk_merch_product_creator FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_merch_products_active ON merch_products (active);

-- =========================
-- SAVED PAYMENT METHODS
-- =========================
CREATE TABLE IF NOT EXISTS saved_payment_methods
(
    id                   BIGSERIAL PRIMARY KEY,
    user_id              BIGINT       NOT NULL,
    card_holder_name     VARCHAR(200) NOT NULL,
    last_four            CHAR(4)      NOT NULL,
    brand                VARCHAR(50)  NOT NULL,
    expiry_month         SMALLINT     NOT NULL,
    expiry_year          SMALLINT     NOT NULL,
    encrypted_card_token TEXT         NOT NULL,
    is_default           BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_method_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_payment_methods_user ON saved_payment_methods (user_id);

-- =========================
-- MERCH ORDERS
-- =========================
CREATE TABLE IF NOT EXISTS merch_orders
(
    id                BIGSERIAL PRIMARY KEY,
    user_id           BIGINT         NOT NULL,
    status            VARCHAR(30)    NOT NULL DEFAULT 'PENDING',
    total_amount      NUMERIC(10, 2) NOT NULL,
    payment_method_id BIGINT         NULL,
    created_at        TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_merch_order_user   FOREIGN KEY (user_id)           REFERENCES users                  (id) ON DELETE CASCADE,
    CONSTRAINT fk_merch_order_pm     FOREIGN KEY (payment_method_id) REFERENCES saved_payment_methods  (id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_merch_orders_user ON merch_orders (user_id, created_at DESC);

-- =========================
-- MERCH ORDER ITEMS
-- =========================
CREATE TABLE IF NOT EXISTS merch_order_items
(
    id                   BIGSERIAL PRIMARY KEY,
    order_id             BIGINT         NOT NULL,
    product_id           BIGINT         NULL,
    product_name_snapshot VARCHAR(200)  NOT NULL,
    price_snapshot       NUMERIC(10, 2) NOT NULL,
    quantity             INT            NOT NULL DEFAULT 1,
    CONSTRAINT fk_order_item_order   FOREIGN KEY (order_id)   REFERENCES merch_orders   (id) ON DELETE CASCADE,
    CONSTRAINT fk_order_item_product FOREIGN KEY (product_id) REFERENCES merch_products (id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_order_items_order ON merch_order_items (order_id);

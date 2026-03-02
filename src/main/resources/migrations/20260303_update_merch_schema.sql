-- Merch schema improvements:
--   1. Replace encrypted PAN storage with provider payment token
--   2. Add refund tracking to orders
--   3. Add richer snapshots to order items

-- 1. saved_payment_methods: drop encrypted PAN column, add provider token column
--    The empty-string default allows the ALTER TABLE to succeed on existing rows;
--    the service layer validates that the token is non-blank before any row is saved.
ALTER TABLE saved_payment_methods
    ADD COLUMN IF NOT EXISTS provider_payment_method_id TEXT NOT NULL DEFAULT '';

ALTER TABLE saved_payment_methods
    DROP COLUMN IF EXISTS encrypted_card_token;

-- 2. merch_orders: add refund status and reference fields
ALTER TABLE merch_orders
    ADD COLUMN IF NOT EXISTS refund_status   VARCHAR(20) NOT NULL DEFAULT 'NONE';

ALTER TABLE merch_orders
    ADD COLUMN IF NOT EXISTS refund_reference VARCHAR(200);

-- 3. merch_order_items: add image and category snapshot columns
ALTER TABLE merch_order_items
    ADD COLUMN IF NOT EXISTS image_url_snapshot VARCHAR(500);

ALTER TABLE merch_order_items
    ADD COLUMN IF NOT EXISTS category_snapshot  VARCHAR(100);

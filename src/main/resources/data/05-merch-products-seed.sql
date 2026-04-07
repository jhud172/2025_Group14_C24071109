-- =========================
-- MERCH PRODUCTS SEED
-- One To One branded apparel
-- Idempotent: safe to run multiple times
-- =========================

INSERT INTO merch_products (name, description, price, image_url, secondary_image_url, stock_quantity, category, active)
SELECT
    'One To One Short Sleeve Logo Top',
    'The official One To One short sleeve compression top. Moisture-wicking fabric designed to support your training. Features the iconic One To One logo on the chest.',
    36.99,
    '/img/Products/Short_Sleeve_Top/Short_Sleeve_Front.jpg',
    '/img/Products/Short_Sleeve_Top/Short_Sleeve_Back.jpg',
    100,
    'Apparel',
    true
WHERE NOT EXISTS (
    SELECT 1 FROM merch_products WHERE name = 'One To One Short Sleeve Logo Top'
);

INSERT INTO merch_products (name, description, price, image_url, secondary_image_url, stock_quantity, category, active)
SELECT
    'One To One Long Sleeve Logo Top',
    'The official One To One long sleeve compression top. Moisture-wicking fabric engineered for high-intensity training in any weather. Features the iconic One To One logo on the chest and back.',
    38.99,
    '/img/Products/Long_Sleeve_Top/Long_Sleeve_Front.jpg',
    '/img/Products/Long_Sleeve_Top/Long_Sleeve_Back.jpg',
    100,
    'Apparel',
    true
WHERE NOT EXISTS (
    SELECT 1 FROM merch_products WHERE name = 'One To One Long Sleeve Logo Top'
);

ALTER TABLE saved_payment_methods
    ALTER COLUMN last_four TYPE VARCHAR(4)
        USING last_four::VARCHAR(4);

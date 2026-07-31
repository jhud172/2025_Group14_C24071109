ALTER TABLE health_records
    ALTER COLUMN systolic_blood_pressure TYPE INTEGER
        USING systolic_blood_pressure::INTEGER,
    ALTER COLUMN diastolic_blood_pressure TYPE INTEGER
        USING diastolic_blood_pressure::INTEGER,
    ALTER COLUMN cholesterol TYPE DOUBLE PRECISION
        USING cholesterol::DOUBLE PRECISION,
    ALTER COLUMN weight_kg TYPE DOUBLE PRECISION
        USING weight_kg::DOUBLE PRECISION,
    ALTER COLUMN height_cm TYPE DOUBLE PRECISION
        USING height_cm::DOUBLE PRECISION,
    ALTER COLUMN bmi TYPE DOUBLE PRECISION
        USING bmi::DOUBLE PRECISION,
    ALTER COLUMN waist_cm TYPE DOUBLE PRECISION
        USING waist_cm::DOUBLE PRECISION;

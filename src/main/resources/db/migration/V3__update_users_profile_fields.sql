ALTER TABLE users
    ADD COLUMN onboarding_completed TINYINT(1) NOT NULL DEFAULT 0,
    ADD COLUMN birth_date DATE NULL;

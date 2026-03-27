ALTER TABLE users
    CHANGE COLUMN nickname name VARCHAR (50) NOT NULL;

ALTER TABLE users
    ADD COLUMN nickname VARCHAR(50) NULL,
    ADD COLUMN onboarding_completed TINYINT(1) NOT NULL DEFAULT 0,
    ADD COLUMN birth_date DATE NULL;

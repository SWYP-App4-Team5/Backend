ALTER TABLE users
    DROP COLUMN notification_all_enabled,
    DROP COLUMN notification_personal_enabled,
    ADD COLUMN notification_daily_enabled  TINYINT(1) NOT NULL DEFAULT 1,
    ADD COLUMN notification_weekly_enabled TINYINT(1) NOT NULL DEFAULT 1;

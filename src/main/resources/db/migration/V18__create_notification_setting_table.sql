CREATE TABLE user_notification_setting
(
    user_id                   BIGINT      NOT NULL PRIMARY KEY,
    daily_enabled             TINYINT(1)  NOT NULL DEFAULT 1,
    weekly_enabled            TINYINT(1)  NOT NULL DEFAULT 1,
    marketing_consent_enabled TINYINT(1)  NOT NULL DEFAULT 0,
    created_at                DATETIME(6) NOT NULL,
    updated_at                DATETIME(6) NOT NULL,
    CONSTRAINT fk_user_notification_setting_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

-- 기존 users 데이터 이전
INSERT INTO user_notification_setting (user_id, daily_enabled, weekly_enabled, marketing_consent_enabled, created_at, updated_at)
SELECT user_id,
       notification_daily_enabled,
       notification_weekly_enabled,
       marketing_consent_enabled,
       NOW(),
       NOW()
FROM users;

-- users 테이블에서 알림 설정 컬럼 제거
ALTER TABLE users
    DROP COLUMN notification_daily_enabled,
    DROP COLUMN notification_weekly_enabled,
    DROP COLUMN marketing_consent_enabled;

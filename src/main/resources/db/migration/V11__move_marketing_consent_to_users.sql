-- users 테이블에 marketing_consent_enabled 컬럼 추가
ALTER TABLE users
    ADD COLUMN marketing_consent_enabled TINYINT(1) NOT NULL DEFAULT 0
        COMMENT '마케팅 수신 동의 여부';

-- user_agreement의 기존 데이터 이전
UPDATE users u
    JOIN user_agreement ua ON u.user_id = ua.user_id
SET u.marketing_consent_enabled = ua.marketing_consent;

-- user_agreement 테이블에서 marketing_consent 컬럼 제거
ALTER TABLE user_agreement
    DROP COLUMN marketing_consent;

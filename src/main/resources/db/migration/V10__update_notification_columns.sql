-- 삭제하는 프로시저 정의
DROP PROCEDURE IF EXISTS modify_notification_table;

DELIMITER $$
CREATE PROCEDURE modify_notification_table()
BEGIN
    -- 사용하지 않는 컬럼 삭제 (존재할 때만 실행)
    IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'notification' AND COLUMN_NAME = 'type' AND TABLE_SCHEMA = DATABASE()) THEN
ALTER TABLE notification DROP COLUMN type;
END IF;

    IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'notification' AND COLUMN_NAME = 'is_sent' AND TABLE_SCHEMA = DATABASE()) THEN
ALTER TABLE notification DROP COLUMN is_sent;
END IF;

    -- 신규 컬럼 추가 및 기존 컬럼 수정 (template_id가 없을 때만 한꺼번에 실행)
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'notification' AND COLUMN_NAME = 'template_id' AND TABLE_SCHEMA = DATABASE()) THEN
ALTER TABLE notification
    ADD COLUMN template_id BIGINT NOT NULL COMMENT '알림 템플릿 ID',
            ADD COLUMN target_token VARCHAR(3000) NOT NULL COMMENT 'FCM 토큰',
            ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '발송 상태',
            ADD COLUMN message_id VARCHAR(200) NOT NULL COMMENT 'FCM 메시지 ID',
            ADD COLUMN fail_code VARCHAR(255) COMMENT '실패 코드',
            ADD COLUMN fail_reason VARCHAR(255) COMMENT '실패 사유',
            MODIFY COLUMN title VARCHAR(3000) NOT NULL,
            MODIFY COLUMN body VARCHAR(3000) NOT NULL;

-- 외래키 추가
ALTER TABLE notification
    ADD CONSTRAINT fk_notification_template_id
        FOREIGN KEY (template_id) REFERENCES notification_template (template_id);
END IF;
END $$
DELIMITER ;

-- 프로시저 실행 및 삭제
CALL modify_notification_table();
DROP PROCEDURE modify_notification_table;

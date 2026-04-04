-- 사용하지 않는 컬럼 삭제
ALTER TABLE notification DROP COLUMN type;
ALTER TABLE notification DROP COLUMN is_sent;

-- 신규 컬럼 추가 및 기존 컬럼 수정
ALTER TABLE notification
    ADD COLUMN template_id BIGINT NOT NULL COMMENT '알림 템플릿 ID',
    ADD COLUMN target_token VARCHAR(3000) NOT NULL COMMENT 'FCM 토큰',
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '발송 상태',
    ADD COLUMN message_id VARCHAR(200) NOT NULL COMMENT 'FCM 메시지 ID',
    ADD COLUMN fail_code VARCHAR(255) COMMENT '실패 코드',
    ADD COLUMN fail_reason VARCHAR(255) COMMENT '실패 사유',
    MODIFY COLUMN title VARCHAR(3000) NOT NULL,
    MODIFY COLUMN body VARCHAR(3000) NOT NULL;

ALTER TABLE notification
    ADD CONSTRAINT fk_notification_template_id
        FOREIGN KEY (template_id) REFERENCES notification_template (template_id);

-- message_id의 경우 엔티티에서 nullable = false이므로,
-- 기존 데이터가 있다면 적절한 더미값 처리 후 NOT NULL 설정을 해야 함.
-- 기존 데이터가 없다면 위 ADD COLUMN 시 바로 NOT NULL을 붙여도 무방함.
-- ALTER TABLE notification MODIFY COLUMN message_id VARCHAR(200) NOT NULL;

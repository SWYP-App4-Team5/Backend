DROP PROCEDURE IF EXISTS rename_column_if_exists;

DELIMITER $$
CREATE PROCEDURE rename_column_if_exists()
BEGIN
    -- 컬럼이 존재하면 변경 실행
    IF EXISTS (
        SELECT * FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_NAME = 'user_device'
        AND COLUMN_NAME = 'user_device_id'
        AND TABLE_SCHEMA = DATABASE()
    ) THEN
ALTER TABLE `user_device`
    CHANGE COLUMN `user_device_id` `device_id` BIGINT NOT NULL AUTO_INCREMENT;
END IF;
END $$
DELIMITER ;

CALL rename_column_if_exists();
DROP PROCEDURE rename_column_if_exists;

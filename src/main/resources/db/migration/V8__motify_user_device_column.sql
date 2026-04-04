-- PK 컬럼명 변경
ALTER TABLE `user_device`
    CHANGE COLUMN `user_device_id` `device_id` BIGINT NOT NULL AUTO_INCREMENT;

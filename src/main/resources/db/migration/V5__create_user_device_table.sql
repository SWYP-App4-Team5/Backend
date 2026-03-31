CREATE TABLE user_device
(
    user_device_id BIGINT       NOT NULL AUTO_INCREMENT,
    user_id        BIGINT       NOT NULL,
    device_uuid    VARCHAR(100) NOT NULL,
    fcm_token      VARCHAR(300) NOT NULL,
    is_active      TINYINT(1) NOT NULL DEFAULT 1,
    created_at     DATETIME     NOT NULL,
    updated_at     DATETIME     NOT NULL,
    PRIMARY KEY (user_device_id),
    CONSTRAINT uk_user_device_fcm_token UNIQUE (fcm_token),
    CONSTRAINT fk_user_device_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE INDEX idx_user_device_user_id
    ON user_device (user_id);

CREATE INDEX idx_user_device_device_uuid
    ON user_device (device_uuid);
s

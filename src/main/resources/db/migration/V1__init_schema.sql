CREATE TABLE users
(
    user_id                       BIGINT       NOT NULL AUTO_INCREMENT,
    nickname                      VARCHAR(50)  NOT NULL,
    email                         VARCHAR(255) NULL,
    profile_image_url             VARCHAR(255) NULL,
    provider                      VARCHAR(50)  NOT NULL,
    provider_id                   VARCHAR(255) NOT NULL,
    notification_all_enabled      TINYINT(1) NOT NULL DEFAULT 0,
    notification_personal_enabled TINYINT(1) NOT NULL DEFAULT 0,
    last_login_at                 DATETIME(6) NOT NULL,
    created_at                    DATETIME(6) NULL,
    updated_at                    DATETIME(6) NULL,
    PRIMARY KEY (user_id),
    CONSTRAINT uk_users_provider_provider_id UNIQUE (provider, provider_id)
);

CREATE INDEX idx_provider ON users (provider, provider_id);
CREATE INDEX idx_email ON users (email);

CREATE TABLE refresh_token
(
    refresh_token_id BIGINT       NOT NULL AUTO_INCREMENT,
    user_id          BIGINT NULL,
    token            VARCHAR(512) NOT NULL,
    expires_at       DATETIME(6) NOT NULL,
    created_at       DATETIME(6) NULL,
    updated_at       DATETIME(6) NULL,
    PRIMARY KEY (refresh_token_id),
    CONSTRAINT uk_refresh_token_token UNIQUE (token)
);

CREATE INDEX idx_refresh_token_user_id ON refresh_token (user_id);
CREATE INDEX idx_refresh_token_token ON refresh_token (token);

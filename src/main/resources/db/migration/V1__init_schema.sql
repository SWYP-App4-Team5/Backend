CREATE TABLE users
(
    user_id                       BIGINT       NOT NULL AUTO_INCREMENT,
    nickname                      VARCHAR(50)  NOT NULL,
    email                         VARCHAR(255) NULL,
    profile_image_url             VARCHAR(255) NULL,
    provider                      VARCHAR(50)  NOT NULL,
    provider_id                   VARCHAR(255) NOT NULL,
    notification_all_enabled      TINYINT(1)   NOT NULL DEFAULT 0,
    notification_personal_enabled TINYINT(1)   NOT NULL DEFAULT 0,
    last_login_at                 DATETIME(6)  NOT NULL,
    created_at                    DATETIME(6)  NULL,
    updated_at                    DATETIME(6)  NULL,
    PRIMARY KEY (user_id),
    CONSTRAINT uk_users_provider_provider_id UNIQUE (provider, provider_id)
);

CREATE INDEX idx_provider ON users (provider, provider_id);
CREATE INDEX idx_email ON users (email);

CREATE TABLE refresh_token
(
    refresh_token_id BIGINT       NOT NULL AUTO_INCREMENT,
    user_id          BIGINT       NULL,
    token            VARCHAR(512) NOT NULL,
    expires_at       DATETIME(6)  NOT NULL,
    created_at       DATETIME(6)  NULL,
    updated_at       DATETIME(6)  NULL,
    PRIMARY KEY (refresh_token_id),
    CONSTRAINT uk_refresh_token_token UNIQUE (token)
);

CREATE INDEX idx_refresh_token_user_id ON refresh_token (user_id);
CREATE INDEX idx_refresh_token_token ON refresh_token (token);

-- category.name은 Java enum 상수명으로 저장 (@Enumerated(EnumType.STRING))
CREATE TABLE category
(
    category_id    BIGINT      NOT NULL AUTO_INCREMENT,
    name           ENUM('FOOD_DELIVERY','CAFE_DESSERT','TRANSPORT','FASHION_BEAUTY','HOBBY_CULTURE','ALCOHOL_ENTERTAINMENT','OTHER') NOT NULL,
    default_amount BIGINT      NOT NULL,
    icon_url       TEXT        NULL,
    sort_order     INT         NOT NULL,
    PRIMARY KEY (category_id)
);

CREATE TABLE category_amount_option
(
    option_id   BIGINT      NOT NULL AUTO_INCREMENT,
    amount      BIGINT      NOT NULL,
    sort_order  INT         NOT NULL,
    created_at  DATETIME(6) NOT NULL,
    updated_at  DATETIME(6) NOT NULL,
    category_id BIGINT      NOT NULL,
    PRIMARY KEY (option_id),
    CONSTRAINT FK_category_TO_category_amount_option_1
        FOREIGN KEY (category_id) REFERENCES category (category_id)
);

CREATE TABLE challenge_min_goal_policy
(
    policy_id    BIGINT      NOT NULL AUTO_INCREMENT,
    member_count INT         NOT NULL UNIQUE,
    min_amount   BIGINT      NOT NULL,
    created_at   DATETIME(6) NOT NULL,
    updated_at   DATETIME(6) NOT NULL,
    PRIMARY KEY (policy_id)
);

CREATE TABLE team
(
    team_id              BIGINT       NOT NULL AUTO_INCREMENT,
    team_name            VARCHAR(100) NULL,
    type                 ENUM('FRIEND','COUPLE','FAMILY','CLUB','OTHER')  NOT NULL,
    invite_code          VARCHAR(30)  NOT NULL,
    current_member_count INT          NOT NULL DEFAULT 1,
    max_member_count     INT          NOT NULL DEFAULT 8,
    created_at           DATETIME(6)  NOT NULL,
    updated_at           DATETIME(6)  NOT NULL,
    PRIMARY KEY (team_id),
    CONSTRAINT UQ_invite_code UNIQUE (invite_code)
);

CREATE TABLE team_members
(
    team_id   BIGINT      NOT NULL,
    user_id   BIGINT      NOT NULL,
    role      VARCHAR(20) NOT NULL,
    joined_at DATETIME(6) NOT NULL,
    PRIMARY KEY (team_id, user_id),
    CONSTRAINT FK_team_TO_team_members_1
        FOREIGN KEY (team_id) REFERENCES team (team_id),
    CONSTRAINT FK_users_TO_team_members_1
        FOREIGN KEY (user_id) REFERENCES users (user_id)
);

-- status에 CANCELLED 포함 (V4 반영)
CREATE TABLE challenge
(
    challenge_id             BIGINT       NOT NULL AUTO_INCREMENT,
    title                    VARCHAR(100) NOT NULL,
    description              VARCHAR(255) NULL,
    goal_amount              BIGINT       NOT NULL,
    min_personal_goal_amount BIGINT       NOT NULL,
    status                   ENUM('WAITING','ONGOING','COMPLETED','FAILED','CANCELLED')  NOT NULL,
    start_date               DATETIME(6)  NOT NULL,
    end_date                 DATETIME(6)  NOT NULL,
    created_at               DATETIME(6)  NOT NULL,
    updated_at               DATETIME(6)  NOT NULL,
    team_id                  BIGINT       NOT NULL,
    PRIMARY KEY (challenge_id),
    CONSTRAINT FK_team_TO_challenge_1
        FOREIGN KEY (team_id) REFERENCES team (team_id)
);

CREATE TABLE challenge_category
(
    challenge_id BIGINT NOT NULL,
    category_id  BIGINT NOT NULL,
    amount       BIGINT NOT NULL,
    PRIMARY KEY (challenge_id, category_id),
    CONSTRAINT FK_challenge_TO_challenge_category_1
        FOREIGN KEY (challenge_id) REFERENCES challenge (challenge_id),
    CONSTRAINT FK_category_TO_challenge_category_1
        FOREIGN KEY (category_id) REFERENCES category (category_id)
);

-- updated_at 추가 (V3 반영)
CREATE TABLE challenge_week
(
    week_id           BIGINT      NOT NULL AUTO_INCREMENT,
    week_number       INT         NOT NULL DEFAULT 1,
    week_goal_amount  BIGINT      NOT NULL,
    week_saved_amount BIGINT      NOT NULL DEFAULT 0,
    start_date        DATETIME(6) NOT NULL,
    end_date          DATETIME(6) NOT NULL,
    created_at        DATETIME(6) NOT NULL,
    updated_at        DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    challenge_id      BIGINT      NOT NULL,
    PRIMARY KEY (week_id),
    CONSTRAINT FK_challenge_TO_challenge_week_1
        FOREIGN KEY (challenge_id) REFERENCES challenge (challenge_id)
);

CREATE TABLE certification
(
    certification_id BIGINT      NOT NULL AUTO_INCREMENT,
    spend_type       VARCHAR(20) NOT NULL,
    spend_amount     BIGINT      NOT NULL,
    memo             VARCHAR(256) NOT NULL,
    image_url        TEXT        NULL,
    spent_at         DATETIME(6) NOT NULL,
    saved_amount     BIGINT      NOT NULL,
    created_at       DATETIME(6) NOT NULL,
    updated_at       DATETIME(6) NOT NULL,
    challenge_id     BIGINT      NOT NULL,
    user_id          BIGINT      NOT NULL,
    category_id      BIGINT      NOT NULL,
    week_id          BIGINT      NOT NULL,
    PRIMARY KEY (certification_id),
    CONSTRAINT FK_challenge_TO_certification_1
        FOREIGN KEY (challenge_id) REFERENCES challenge (challenge_id),
    CONSTRAINT FK_users_TO_certification_1
        FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT FK_category_TO_certification_1
        FOREIGN KEY (category_id) REFERENCES category (category_id),
    CONSTRAINT FK_challenge_week_TO_certification_1
        FOREIGN KEY (week_id) REFERENCES challenge_week (week_id)
);

CREATE INDEX idx_certification_created_at ON certification (created_at);

-- updated_at 추가 (V3 반영)
CREATE TABLE certification_like
(
    like_id          BIGINT      NOT NULL AUTO_INCREMENT,
    created_at       DATETIME(6) NOT NULL,
    updated_at       DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    deleted_at       DATETIME(6) NULL,
    certification_id BIGINT      NOT NULL,
    user_id          BIGINT      NOT NULL,
    PRIMARY KEY (like_id),
    CONSTRAINT FK_certification_TO_certification_like_1
        FOREIGN KEY (certification_id) REFERENCES certification (certification_id),
    CONSTRAINT FK_users_TO_certification_like_1
        FOREIGN KEY (user_id) REFERENCES users (user_id)
);

-- updated_at 추가 (V3 반영)
CREATE TABLE challenge_member_result
(
    result_id                 BIGINT       NOT NULL AUTO_INCREMENT,
    total_saved_amount        BIGINT       NOT NULL,
    total_cert_count          INT          NOT NULL,
    is_personal_success       TINYINT(1)   NOT NULL,
    is_rule_violated          TINYINT(1)   NOT NULL DEFAULT 0,
    streak_days               INT          NOT NULL DEFAULT 0,
    weekly_participation_rate DECIMAL(5,2) NOT NULL DEFAULT 0,
    created_at                DATETIME(6)  NOT NULL,
    updated_at                DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    user_id                   BIGINT       NOT NULL,
    challenge_id              BIGINT       NOT NULL,
    PRIMARY KEY (result_id),
    CONSTRAINT FK_users_TO_challenge_member_result_1
        FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT FK_challenge_TO_challenge_member_result_1
        FOREIGN KEY (challenge_id) REFERENCES challenge (challenge_id)
);

-- updated_at 추가 (V3 반영)
CREATE TABLE challenge_team_result
(
    result_id                     BIGINT       NOT NULL AUTO_INCREMENT,
    goal_amount                   BIGINT       NOT NULL,
    total_saved_amount            BIGINT       NOT NULL,
    total_cert_count              INT          NOT NULL,
    is_team_success               TINYINT(1)   NOT NULL,
    team_streak_days              INT          NOT NULL DEFAULT 0,
    achievement_rate              DECIMAL(5,2) NOT NULL DEFAULT 0,
    avg_weekly_cert_count         DECIMAL(5,2) NOT NULL DEFAULT 0,
    avg_weekly_participation_rate DECIMAL(5,2) NOT NULL DEFAULT 0,
    created_at                    DATETIME(6)  NOT NULL,
    updated_at                    DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    challenge_id                  BIGINT       NOT NULL,
    PRIMARY KEY (result_id),
    CONSTRAINT FK_challenge_TO_challenge_team_result_1
        FOREIGN KEY (challenge_id) REFERENCES challenge (challenge_id)
);

CREATE TABLE user_agreement
(
    user_agreement_id       BIGINT     NOT NULL AUTO_INCREMENT,
    age_verified            TINYINT(1) NOT NULL DEFAULT 0,
    terms_of_service_agreed TINYINT(1) NOT NULL DEFAULT 0,
    privacy_policy_agreed   TINYINT(1) NOT NULL DEFAULT 0,
    marketing_consent       TINYINT(1) NOT NULL DEFAULT 0,
    user_id                 BIGINT     NOT NULL,
    PRIMARY KEY (user_agreement_id),
    CONSTRAINT FK_users_TO_user_agreement_1
        FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE user_device
(
    device_id   BIGINT       NOT NULL AUTO_INCREMENT,
    fcm_token   VARCHAR(300) NOT NULL,
    device_type ENUM('IOS','ANDROID')  NOT NULL,
    device_uuid VARCHAR(100) NOT NULL,
    created_at  DATETIME(6)  NOT NULL,
    updated_at  DATETIME(6)  NOT NULL,
    user_id     BIGINT       NOT NULL,
    is_active   TINYINT(1)   NOT NULL DEFAULT 1,
    PRIMARY KEY (device_id),
    CONSTRAINT FK_users_TO_user_device_1
        FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE notification_template
(
    template_id BIGINT       NOT NULL AUTO_INCREMENT,
    type        ENUM('ENCOURAGE','LIKE','GOAL_NEAR','GOAL_COMPLETE') NOT NULL,
    title       VARCHAR(100) NOT NULL,
    body        VARCHAR(200) NOT NULL,
    created_at  DATETIME(6)  NOT NULL,
    updated_at  DATETIME(6)  NOT NULL,
    PRIMARY KEY (template_id)
);

-- updated_at 추가 (V3 반영)
CREATE TABLE notification
(
    notification_id BIGINT       NOT NULL AUTO_INCREMENT,
    type            ENUM('ENCOURAGE','LIKE','GOAL_NEAR','GOAL_COMPLETE') NOT NULL,
    title           VARCHAR(100) NULL,
    body            VARCHAR(200) NOT NULL,
    is_read         TINYINT(1)   NOT NULL DEFAULT 0,
    is_sent         TINYINT(1)   NOT NULL DEFAULT 0,
    created_at      DATETIME(6)  NOT NULL,
    updated_at      DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    related_id      BIGINT       NULL,
    user_id         BIGINT       NOT NULL,
    PRIMARY KEY (notification_id),
    CONSTRAINT FK_users_TO_notification_1
        FOREIGN KEY (user_id) REFERENCES users (user_id)
);

-- created_at, updated_at 추가 (V3 반영)
CREATE TABLE terms
(
    terms_id   BIGINT       NOT NULL AUTO_INCREMENT,
    type       ENUM('SERVICE_TERMS') NOT NULL,
    version    VARCHAR(20)  NOT NULL,
    title      VARCHAR(200) NOT NULL,
    content    VARCHAR(500) NOT NULL,
    created_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (terms_id)
);

CREATE TABLE item
(
    item_id     BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    price       BIGINT       NOT NULL,
    sort_order  INT          NOT NULL,
    created_at  DATETIME(6)  NOT NULL,
    updated_at  DATETIME(6)  NOT NULL,
    category_id BIGINT       NOT NULL,
    PRIMARY KEY (item_id),
    CONSTRAINT FK_category_TO_item_1
        FOREIGN KEY (category_id) REFERENCES category (category_id)
);

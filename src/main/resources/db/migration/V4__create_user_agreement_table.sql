CREATE TABLE IF NOT EXISTS user_agreement
(
    user_agreement_id       BIGINT   NOT NULL AUTO_INCREMENT,
    user_id                 BIGINT   NOT NULL,
    age_verified            TINYINT(1) NOT NULL DEFAULT 0,
    terms_of_service_agreed TINYINT(1) NOT NULL DEFAULT 0,
    privacy_policy_agreed   TINYINT(1) NOT NULL DEFAULT 0,
    marketing_consent       TINYINT(1) NOT NULL DEFAULT 0,
    created_at              DATETIME NOT NULL,
    updated_at              DATETIME NOT NULL,
    PRIMARY KEY (user_agreement_id),
    CONSTRAINT uk_user_agreement_user_id UNIQUE (user_id),
    CONSTRAINT fk_user_agreement_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

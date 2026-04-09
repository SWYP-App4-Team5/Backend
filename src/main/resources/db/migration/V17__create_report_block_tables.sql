CREATE TABLE report
(
    report_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    reporter_id      BIGINT       NOT NULL,
    reported_id      BIGINT       NOT NULL,
    challenge_id     BIGINT       NOT NULL,
    created_at       DATETIME(6)  NOT NULL,
    updated_at       DATETIME(6)  NOT NULL,
    CONSTRAINT fk_report_reporter FOREIGN KEY (reporter_id) REFERENCES users (user_id),
    CONSTRAINT fk_report_reported FOREIGN KEY (reported_id) REFERENCES users (user_id),
    CONSTRAINT fk_report_challenge FOREIGN KEY (challenge_id) REFERENCES challenge (challenge_id)
);

CREATE TABLE report_reason
(
    report_reason_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    report_id        BIGINT      NOT NULL,
    reason           VARCHAR(50) NOT NULL,
    CONSTRAINT fk_report_reason_report FOREIGN KEY (report_id) REFERENCES report (report_id)
);

CREATE TABLE block
(
    block_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    blocker_id   BIGINT      NOT NULL,
    blocked_id   BIGINT      NOT NULL,
    challenge_id BIGINT      NOT NULL,
    created_at   DATETIME(6) NOT NULL,
    updated_at   DATETIME(6) NOT NULL,
    CONSTRAINT fk_block_blocker FOREIGN KEY (blocker_id) REFERENCES users (user_id),
    CONSTRAINT fk_block_blocked FOREIGN KEY (blocked_id) REFERENCES users (user_id),
    CONSTRAINT fk_block_challenge FOREIGN KEY (challenge_id) REFERENCES challenge (challenge_id),
    CONSTRAINT uq_block UNIQUE (blocker_id, blocked_id, challenge_id)
);

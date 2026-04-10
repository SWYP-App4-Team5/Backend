-- report 테이블에 target_type(USER/CERTIFICATION) 구분 컬럼 추가
ALTER TABLE report ADD COLUMN target_type VARCHAR(30) NOT NULL DEFAULT 'USER';

-- 게시글 신고 시 대상 인증 ID (USER 신고는 NULL)
ALTER TABLE report ADD COLUMN certification_id BIGINT NULL;
ALTER TABLE report ADD CONSTRAINT fk_report_certification
    FOREIGN KEY (certification_id) REFERENCES certification (certification_id);

-- 기존 데이터는 USER 신고이므로 reported_id를 nullable로 변경 (게시글 신고 시 NULL 가능)
ALTER TABLE report MODIFY COLUMN reported_id BIGINT NULL;

-- certification 테이블에 비노출 컬럼 추가 (신고 즉시 숨김 처리)
ALTER TABLE certification ADD COLUMN is_hidden TINYINT(1) NOT NULL DEFAULT 0;

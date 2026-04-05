ALTER TABLE notification_template ADD COLUMN sub_type VARCHAR(50);

-- 2. 기존 데이터의 sub_type 채우기 (기존 type이 ENCOURAGE였다면 DAILY 등으로 기본값 세팅)
-- 이 단계를 생략하면 기존 데이터의 sub_type은 NULL로 남습니다.
UPDATE notification_template
SET sub_type = 'DAILY'
WHERE sub_type IS NULL;

-- 기존 Unique 제약 조건 삭제 및 신규 복합 Unique 제약 조건 생성
-- 기존: type 중복 불가 -> 변경: type + sub_type 조합이 중복 불가
ALTER TABLE notification_template DROP INDEX UQ_template_type;
ALTER TABLE notification_template ADD CONSTRAINT UQ_template_type_subtype UNIQUE (type, sub_type);

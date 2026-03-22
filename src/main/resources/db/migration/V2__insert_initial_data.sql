-- V2__insert_initial_data.sql


-- 7개 고정 카테고리 (name 컬럼은 Java enum 상수명으로 저장 - @Enumerated(EnumType.STRING))
INSERT INTO category (name, default_amount, sort_order)
VALUES ('FOOD_DELIVERY', 18000, 1),
       ('CAFE_DESSERT', 4500, 2),
       ('TRANSPORT', 3000, 3),
       ('FASHION_BEAUTY', 30000, 4),
       ('HOBBY_CULTURE', 10000, 5),
       ('ALCOHOL_ENTERTAINMENT', 10000, 6),
       ('OTHER', 5000, 7);

-- 카테고리별 금액 선택지
INSERT INTO category_amount_option (amount, sort_order, created_at, updated_at, category_id)
VALUES
-- 외식/배달 (category_id = 1)
(10000, 1, NOW(), NOW(), 1),
(15000, 2, NOW(), NOW(), 1),
(20000, 3, NOW(), NOW(), 1),
(30000, 4, NOW(), NOW(), 1),

-- 카페/디저트 (category_id = 2)
(1500, 1, NOW(), NOW(), 2),
(2000, 2, NOW(), NOW(), 2),
(4000, 3, NOW(), NOW(), 2),
(7000, 4, NOW(), NOW(), 2),

-- 교통 (category_id = 3)
(1500, 1, NOW(), NOW(), 3),
(3000, 2, NOW(), NOW(), 3),
(5000, 3, NOW(), NOW(), 3),
(10000, 4, NOW(), NOW(), 3),

-- 패션/뷰티 (category_id = 4)
(5000, 1, NOW(), NOW(), 4),
(10000, 2, NOW(), NOW(), 4),
(20000, 3, NOW(), NOW(), 4),
(50000, 4, NOW(), NOW(), 4),

-- 취미/문화 (category_id = 5)
(5000, 1, NOW(), NOW(), 5),
(10000, 2, NOW(), NOW(), 5),
(20000, 3, NOW(), NOW(), 5),
(30000, 4, NOW(), NOW(), 5),

-- 술/유흥 (category_id = 6)
(10000, 1, NOW(), NOW(), 6),
(30000, 2, NOW(), NOW(), 6),
(50000, 3, NOW(), NOW(), 6),
(100000, 4, NOW(), NOW(), 6),

-- 기타 (category_id = 7)
(5000, 1, NOW(), NOW(), 7),
(10000, 2, NOW(), NOW(), 7),
(20000, 3, NOW(), NOW(), 7);

-- 인원수별 최소 목표 금액 정책
-- 챌린지 시작과 동시에 최소 금액 미만인 팀은 강제 조정됨
INSERT INTO challenge_min_goal_policy (member_count, min_amount, created_at, updated_at)
VALUES (2, 10000, NOW(), NOW()),
       (3, 15000, NOW(), NOW()),
       (4, 20000, NOW(), NOW()),
       (5, 25000, NOW(), NOW()),
       (6, 30000, NOW(), NOW()),
       (7, 35000, NOW(), NOW()),
       (8, 40000, NOW(), NOW());
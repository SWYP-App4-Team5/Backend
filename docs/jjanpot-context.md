# 짠팟 (Jjanpot) 프로젝트 컨텍스트

## 서비스 개요

팀 단위 절약 챌린지 플랫폼. 팀원들이 함께 절약 목표를 설정하고, 절약 인증을 통해 목표를 달성하는 서비스.

## 기술 스택

- **백엔드**: Spring Boot, JPA/Hibernate, MySQL
- **인프라**: AWS EC2, RDS, S3
- **알림**: FCM (Firebase Cloud Messaging)
- **배포**: GitHub Actions CI/CD, Docker Hub + EC2 SSH

---

## 배포 전략 (블루그린 무중단 배포)

### 포트 구성

```
Blue  서버 → 8081
Green 서버 → 8082
Nginx → 현재 활성 포트로 upstream 전환
```

### 배포 흐름

```
현재 Blue(8081) 서비스 중
  → Green(8082)에 새 버전 Docker 컨테이너 배포
  → GET /health 로 Green 정상 확인
  → Nginx upstream 포트 전환 (8081 → 8082)
  → Blue 컨테이너 종료
```

### 관련 엔드포인트

| 엔드포인트 | 설명 |
|---|---|
| `GET /env` | 현재 활성 서버 환경 확인 (`app.env` 값 반환) |
| `GET /health` | 헬스체크 - Nginx가 정상 여부 판단 |
| `GET /version` | 현재 배포된 애플리케이션 버전 확인 (`app.version` 값 반환) |

### 환경변수 주입 (배포 시)

```
app.env     → blue / green
app.version → 배포 버전 (예: 1.0.0)
```

## 패키지 구조

```
com.jjanpot.server
  ├── domain
  │   ├── auth         entity(RefreshToken) / controller / service / repository / dto / client
  │   ├── user         entity(User) / repository
  │   ├── team         entity(Team, TeamMembers, TeamType, TeamRole) / repository
  │   ├── challenge    entity(Challenge, ChallengeWeek, ChallengeCategory, ...) / controller / service / repository / dto
  │   ├── certification entity(Certification, CertificationLike, SpendType)
  │   ├── category     entity(Category, CategoryAmountOption, CategoryName) / repository
  │   ├── notification entity(Notification, NotificationTemplate, NotificationType)
  │   ├── item         entity(Item)
  │   └── terms        entity(Terms, TermsType)
  └── global
      ├── entity       (BaseEntity)
      ├── auth
      ├── aop
      ├── config
      ├── exception    (BusinessException, ErrorCode, GlobalExceptionHandler)
      ├── security     (JWT 인증 필터, JwtTokenProvider)
      └── swagger
```

---

## 서비스 흐름

### 챌린지 생성 흐름

```
챌린지 생성 (팀장)
  → team + challenge + team_members + challenge_week 동시 생성 (트랜잭션)
  → 초대 코드 발급
  → 팀원 초대
  → 팀원 합류 시 team_members row 추가
```

### 인증 흐름

```
유저 인증 작성
  → spend_type (SPEND / NO_SPEND) 선택
  → spend_amount 입력 (실제 사용 금액)
  → saved_amount 서버에서 계산 (기준금액 - spend_amount, +/- 가능)
  → certification 저장
  → challenge_week.week_saved_amount 누적 (트랜잭션으로 묶음)
```

### 챌린지 종료 흐름

```
Spring Scheduler (매일 자정)
  → 종료일 도달한 챌린지 조회
  → 개인 성공/실패 판단 → challenge_member_result 저장
  → 팀 성공/실패 판단 → challenge_team_result 저장
  → challenge.status COMPLETED / FAILED 변경
```

---

## 테이블 설계 (총 22개)

### 1. users

```sql
user_id
BIGINT PK
nickname                      VARCHAR(50) NOT NULL
email                         VARCHAR(100) NULL
profile_image_url             VARCHAR(1024) NULL
provider                      ENUM('KAKAO') NOT NULL
provider_id                   VARCHAR(1024) NOT NULL
last_login_at                 DATETIME NOT NULL
notification_all_enabled      TINYINT(1) NOT NULL
notification_personal_enabled TINYINT(1) NOT NULL
created_at                    DATETIME NOT NULL
updated_at                    DATETIME NOT NULL
```

### 2. user_device

```sql
device_id
BIGINT PK
fcm_token    VARCHAR(300) NOT NULL  -- 로그인 시 갱신
device_type  ENUM('IOS','ANDROID') NOT NULL
device_uuid  VARCHAR(100) NOT NULL UNIQUE  -- iOS:IDFV, Android:AndroidID
is_active    TINYINT(1) NOT NULL DEFAULT true  -- UNREGISTERED 에러 시 false
created_at   DATETIME NOT NULL
updated_at   DATETIME NOT NULL
user_id      BIGINT FK
→ users
```

### 3. user_agreement

```sql
user_agreement_id
BIGINT PK
age_verified            TINYINT(1) NOT NULL DEFAULT false
terms_of_service_agreed TINYINT(1) NOT NULL DEFAULT false
privacy_policy_agreed   TINYINT(1) NOT NULL DEFAULT false
marketing_consent       TINYINT(1) NOT NULL DEFAULT false
user_id                 BIGINT FK
→ users  UNIQUE
```

### 4. refresh_token

```sql
refresh_token_id
BIGINT PK
token            VARCHAR(512) NOT NULL
expires_at       DATETIME NOT NULL
user_id          BIGINT FK
→ users
```

### 5. team

```sql
team_id
BIGINT PK
team_name             VARCHAR(100) NOT NULL
invite_code           VARCHAR(30) NOT NULL UNIQUE
type                  ENUM('FRIEND','COUPLE','FAMILY','CLUB','OTHER') NOT NULL  -- 팀 유형
current_member_count  INT NOT NULL DEFAULT 1  -- 현재 참여 인원 (팀장 포함)
min_member_count      INT NOT NULL DEFAULT 2
max_member_count      INT NOT NULL  -- 유저가 설정, 2~8명
created_at            DATETIME NOT NULL
updated_at            DATETIME NOT NULL
```

### 6. team_members

```sql
team_id
BIGINT PK+FK
→ team
user_id   BIGINT PK+FK
→ users
role      ENUM('LEADER','MEMBER') NOT NULL
joined_at DATETIME NOT NULL
```

### 7. challenge

```sql
challenge_id
BIGINT PK
title                     VARCHAR(100) NOT NULL  -- 챌린지 제목 (MVP: 팀명과 동일)
memo                      VARCHAR(200) NULL       -- 챌린지 메모 (컬럼명: memo, 필드명: description)
goal_amount               BIGINT NOT NULL  -- 최소: 인원수별 정책, 최대: 3,000,000원
min_personal_goal_amount  BIGINT NOT NULL  -- 최소: 5,000원, 최대: 300,000원
status                    ENUM('WAITING','ONGOING','COMPLETED','FAILED') NOT NULL
start_date                DATETIME NOT NULL  -- 시작 당일 00시
end_date                  DATETIME NOT NULL  -- start_date + 7일 (MVP)
created_at                DATETIME NOT NULL
updated_at                DATETIME NOT NULL
team_id                   BIGINT FK
→ team
-- 주의: 이전 type 컬럼(ChallengeType)은 team.type(TeamType)으로 이동됨
```

### 8. challenge_week

```sql
week_id
BIGINT PK
week_number       INT NOT NULL DEFAULT 1  -- MVP: 항상 1
week_goal_amount  BIGINT NOT NULL  -- MVP: challenge.goal_amount와 동일
week_saved_amount BIGINT NOT NULL DEFAULT 0  -- 인증마다 누적
start_date        DATETIME NOT NULL
end_date          DATETIME NOT NULL
created_at        DATETIME NOT NULL
challenge_id      BIGINT FK
→ challenge
UNIQUE (challenge_id, week_number)
```

### 9. challenge_category

```sql
challenge_id
BIGINT PK+FK
→ challenge
category_id  BIGINT PK+FK
→ category
amount       BIGINT NOT NULL  -- 팀장이 설정한 카테고리 기준금액
```

### 10. challenge_team_result

```sql
result_id
BIGINT PK
goal_amount                    BIGINT NOT NULL  -- 스냅샷
total_saved_amount             BIGINT NOT NULL
total_cert_count               INT NOT NULL
is_team_success                TINYINT(1) NOT NULL
team_streak_days               INT NOT NULL DEFAULT 0
achievement_rate               DECIMAL(5,2) NOT NULL DEFAULT 0
avg_weekly_cert_count          DECIMAL(5,2) NOT NULL DEFAULT 0
avg_weekly_participation_rate  DECIMAL(5,2) NOT NULL DEFAULT 0
created_at                     DATETIME NOT NULL
challenge_id                   BIGINT FK
→ challenge  UNIQUE
```

### 11. challenge_member_result

```sql
result_id
BIGINT PK
total_saved_amount         BIGINT NOT NULL
cert_count                 INT NOT NULL
is_personal_success        TINYINT(1) NOT NULL
is_rule_violated           TINYINT(1) NOT NULL DEFAULT false
streak_days                INT NOT NULL DEFAULT 0
weekly_participation_rate  DECIMAL(5,2) NOT NULL DEFAULT 0
created_at                 DATETIME NOT NULL
user_id                    BIGINT FK
→ users
challenge_id               BIGINT FK
→ challenge
UNIQUE (challenge_id, user_id)
```

### 12. challenge_min_goal_policy

```sql
policy_id
BIGINT PK
member_count INT NOT NULL UNIQUE  -- 인원 수
min_amount   BIGINT NOT NULL      -- 해당 인원수의 최소 목표 금액
created_at   DATETIME NOT NULL
updated_at   DATETIME NOT NULL
-- 초기 데이터: 2인=10000, 3인=15000, 4인=20000, 5인=25000, 6인=30000, 7인=35000, 8인=40000
```

### 13. certification

```sql
certification_id
BIGINT PK
spend_type       ENUM('SPEND','NO_SPEND') NOT NULL
spend_amount     BIGINT NOT NULL  -- 실제 사용 금액 (무지출=0)
saved_amount     BIGINT NOT NULL  -- 기준금액 - spend_amount (+/- 가능)
memo             VARCHAR(256) NOT NULL
image_url        TEXT NULL
spent_at         DATETIME NOT NULL  -- 실제 지출 발생 시각
created_at       DATETIME NOT NULL
updated_at       DATETIME NOT NULL
challenge_id     BIGINT FK
→ challenge
user_id          BIGINT FK
→ users
category_id      BIGINT FK
→ category
week_id          BIGINT FK
→ challenge_week
```

### 14. certification_like

```sql
like_id
BIGINT PK
created_at       DATETIME NOT NULL
deleted_at       DATETIME NULL  -- Soft Delete (통계 분석용)
certification_id BIGINT FK
→ certification
user_id          BIGINT FK
→ users
-- UNIQUE (certification_id, user_id) → JPA Entity에서 처리
-- Soft Delete라서 좋아요 재활성화 시 deleted_at = null로 UPDATE
```

### 15. category

```sql
category_id
BIGINT PK
name           ENUM('외식/배달','카페/디저트','교통','패션/뷰티','취미/문화생활','술/유흥','기타') NOT NULL
default_amount BIGINT NOT NULL  -- 인증 작성 시 기본값
icon_url       TEXT NULL
sort_order     INT NOT NULL
-- 7개 고정 데이터
```

### 16. category_amount_option

```sql
option_id
BIGINT PK
amount      BIGINT NOT NULL
sort_order  INT NOT NULL
created_at  DATETIME NOT NULL
updated_at  DATETIME NOT NULL
category_id BIGINT FK
→ category
-- 챌린지 생성 시 카테고리별 금액 선택지 (백엔드에서 관리, 배포 없이 수정 가능)
```

### 17. notification_template

```sql
template_id
BIGINT PK
type        ENUM('ENCOURAGE','LIKE','GOAL_NEAR','GOAL_COMPLETE') NOT NULL UNIQUE
title       VARCHAR(100) NOT NULL  -- 변수 포함 가능 ({nickname} 등)
body        VARCHAR(200) NOT NULL  -- 변수 포함 가능
created_at  DATETIME NOT NULL
updated_at  DATETIME NOT NULL
-- FK 없는 독립 테이블
```

### 18. notification

```sql
notification_id
BIGINT PK
type            ENUM('ENCOURAGE','LIKE','GOAL_NEAR','GOAL_COMPLETE') NOT NULL
title           VARCHAR(100) NULL   -- 발송 당시 확정 문구 스냅샷
body            VARCHAR(200) NOT NULL
is_read         TINYINT(1) NOT NULL DEFAULT false
is_sent         TINYINT(1) NOT NULL DEFAULT false  -- false면 재시도 대상
related_id      BIGINT NULL  -- LIKE→certificationId, GOAL→challengeId
created_at      DATETIME NOT NULL
user_id         BIGINT FK
→ users
-- ENCOURAGE는 DB 저장 안 함 (FCM만 발송)
```

### 19. item

```sql
item_id
BIGINT PK
name        VARCHAR(100) NOT NULL  -- "아메리카노 ☕"
price       BIGINT NOT NULL
sort_order  INT NOT NULL
created_at  DATETIME NOT NULL
updated_at  DATETIME NOT NULL
category_id BIGINT FK
→ category
-- 절약 금액 → 물건 환산용 (카테고리별 물건 목록)
```

### 20. terms

```sql
terms_id
BIGINT PK
type       ENUM('SERVICE_TERMS') NOT NULL
version    VARCHAR(20) NOT NULL
title      VARCHAR(200) NOT NULL
content    TEXT NOT NULL
```

### 21~22. (추후 추가 예정)

```sql
-- certification_report (게시물 신고)
-- certification_block  (게시물 차단)
-- user_block           (사용자 차단)
```

---

## 핵심 비즈니스 규칙

### 인증 규칙

```
개인 규칙
  - 주간 인증 횟수 2회 이상 필수
  - 하루 3회 초과 인증 금지

절약 금액 계산
  - 무지출: saved_amount = 기준금액 (양수)
  - 저렴하게 소비: saved_amount = 기준금액 - spend_amount (양수)
  - 기준 초과 소비: saved_amount = 기준금액 - spend_amount (음수)
```

### 챌린지 성공 조건

```
팀 성공: 개인 최소 금액 충족 AND 팀 공동 목표 금액 충족
팀 실패: 위 조건 중 하나라도 미충족
```

### 인증 독려 알림 (ENCOURAGE)

```
- 1일 1회 인증 안 했을 때 → 18시 발송
- 주에 한 번도 인증 안 했을 때 → 3일차, 5일차, 7일차 20시 발송
- notification 테이블에 저장 안 함 (FCM만 발송)
```

### 알림 종류별 저장 정책

```
LIKE, GOAL_NEAR, GOAL_COMPLETE → DB 저장 + FCM 발송
ENCOURAGE → FCM 발송만 (DB 저장 X)
```

### 데이터 정합성 주의사항

```
1. 인증 삭제 시 week_saved_amount도 트랜잭션으로 함께 차감
2. certification_like는 Soft Delete → UNIQUE 제약 없이 deleted_at으로 관리
3. challenge.status는 Scheduler로 자동 변경 (매일 자정)
4. 챌린지 시작 시 goal_amount가 인원수별 최소 금액 미만이면 강제 조정
```

---

## BaseEntity

```java

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	private LocalDateTime updatedAt;
}
```

---

## ENUM 목록

```
TeamType:         FRIEND, COUPLE, FAMILY, CLUB, OTHER  -- (구 ChallengeType → team.type으로 이동)
ChallengeStatus:  WAITING, ONGOING, COMPLETED, FAILED
TeamRole:         LEADER, MEMBER
SpendType:        SPEND, NO_SPEND
DeviceType:       IOS, ANDROID
Provider:         KAKAO
CategoryName:     FOOD_DELIVERY, CAFE_DESSERT, TRANSPORT, FASHION_BEAUTY, HOBBY_CULTURE, ALCOHOL, OTHER
NotificationType: ENCOURAGE, LIKE, GOAL_NEAR, GOAL_COMPLETE
TermsType:        SERVICE_TERMS
```
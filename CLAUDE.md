# 짠팟 (Jjanpot) 백엔드

팀 단위 절약 챌린지 앱 백엔드 서버.

## 참고 문서

- 전체 컨텍스트 (테이블 설계, 비즈니스 규칙, ENUM): `docs/jjanpot-context.md`
- 챌린지 기능 상세: `docs/challenge/challenge-feature.md`
- 백엔드 역할 분배: `docs/backend-role-distribution.md`

---

## 기술 스택

- Java 17, Spring Boot 3.x, JPA/Hibernate, QueryDSL
- MySQL, Flyway (DB 마이그레이션)
- JWT 인증, 소셜 로그인 (카카오/구글/애플)
- AWS EC2, RDS, S3, FCM
- 배포: GitHub Actions CI/CD + Docker Hub + 블루그린 무중단 배포

---

## 코딩 컨벤션

### 엔티티
- `@Builder` + 정적 팩토리 메서드로 생성 (`Team.of(...)`, `Challenge.from(...)`)
- `nullable = false` 컬럼은 primitive 타입 사용 (`int`, `long`)
- `@Id` 필드는 Hibernate 신규 엔티티 판별을 위해 `Long` (wrapper) 유지
- `@NoArgsConstructor(access = AccessLevel.PROTECTED)`

### DTO
- DTO ↔ 엔티티 변환은 DTO의 `from()` 정적 팩토리 메서드 사용
- Request는 `record` 타입 + Bean Validation (`@NotNull`, `@Min`, `@Max` 등)

### 예외 처리
- 비즈니스 예외는 반드시 `BusinessException(ErrorCode.XXX)` 사용
- `IllegalArgumentException` 직접 사용 금지
- `ErrorResponse`는 `GlobalExceptionHandler` 내부에서만 생성

### Controller
- Swagger 명세는 `docs/` 인터페이스로 분리 (`ChallengeControllerV1Docs`)
- 응답은 `SuccessResponse.ok(data)` 또는 `SuccessResponse.created(data)` 사용

---

## 패키지 구조

```
com.jjanpot.server
  ├── domain
  │   ├── auth          소셜 로그인, JWT 발급
  │   ├── user          유저 엔티티, 프로필
  │   ├── team          팀 생성, 초대코드, 팀원 관리
  │   ├── challenge     챌린지 생성, 주차, 카테고리, 결과
  │   ├── certification 절약 인증, 좋아요
  │   ├── category      카테고리 목록, 금액 선택지
  │   ├── notification  FCM 알림, 알림 목록
  │   └── item          절약 금액 환산 물건
  └── global
      ├── exception     BusinessException, ErrorCode, GlobalExceptionHandler
      ├── security      JwtAuthenticationFilter, JwtTokenProvider
      ├── common/dto    SuccessResponse, ErrorResponse
      └── swagger       Swagger 설정, 어노테이션
```

---

## 구현 완료 현황

| 기능 | 브랜치 | 상태 |
|---|---|---|
| 소셜 로그인 (카카오/구글/애플) | - | ✅ |
| JWT 필터 + 인증 경로 설정 | feat/SAT-9 | ✅ |
| 챌린지 생성 `POST /api/challenges/v1` | feat/SAT-9 | ✅ |
| 초대코드 기반 팀 참여 `POST /api/teams/v1/join` | feat/SAT-9 | ✅ |
| 챌린지 생성 단위 테스트 (7개) | feat/SAT-9 | ✅ |

---

## 주요 비즈니스 규칙 요약

- 팀 인원: 최소 2명, 최대 8명
- 카테고리 선택: 최소 1개, 최대 3개
- 챌린지 기간: MVP 기준 1주(7일) 고정
- 초대코드: 6자리, 혼동 문자(O/0/I/1/L) 제외, 32가지 문자셋
- 인증 횟수: 하루 최대 3회, 주간 최소 2회 필수
- 챌린지 상태 자동 전환: Spring Scheduler (매일 자정)
- 팀 성공 조건: 개인 최소 금액 충족 AND 팀 공동 목표 금액 충족

---

## 백엔드 역할 분배 요약

| 담당 | 기능 |
|---|---|
| 나 | 챌린지 생성/참여, 홈 대시보드, 프로필 설정 |
| 팀원1 | 절약 인증 CRUD, 팀 피드, S3 이미지 업로드 |
| 팀원2 | 챌린지 결과 리포트, 마이페이지 |
| 팀원3 | FCM 알림, 챌린지 상태 자동 전환 스케줄러 |

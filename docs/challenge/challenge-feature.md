# 챌린지 기능 구현 문서

> 브랜치: `feat/SAT-9`
> 작업 범위: 챌린지 생성, 팀 참여(초대코드), Security 설정, 단위 테스트

---

## 목차

1. [구현된 API](#1-구현된-api)
2. [챌린지 생성 흐름](#2-챌린지-생성-흐름)
3. [팀 참여 흐름](#3-팀-참여-흐름)
4. [엔티티 설계 결정사항](#4-엔티티-설계-결정사항)
5. [초대코드 설계](#5-초대코드-설계)
6. [Security 설정](#6-security-설정)
7. [예외 처리](#7-예외-처리)
8. [테스트](#8-테스트)
9. [패키지 구조](#9-패키지-구조)

---

## 1. 구현된 API

### 챌린지 생성
```
POST /api/challenges/v1
Authorization: Bearer {token}
```

**Request Body**
```json
{
  "teamName": "절약왕팀",
  "teamType": "FRIEND",
  "memberCount": 4,
  "startDate": "2026-03-25",
  "categories": [
    { "categoryId": 1, "amount": 50000 },
    { "categoryId": 2, "amount": 30000 }
  ],
  "goalAmount": 200000,
  "minPersonalGoalAmount": 30000
}
```

**Response**
```json
{
  "status": 201,
  "message": "생성 완료",
  "data": {
    "challengeId": 1,
    "teamId": 1,
    "teamName": "절약왕팀",
    "inviteCode": "AB3K9P",
    "goalAmount": 200000,
    "minPersonalGoalAmount": 30000,
    "startDate": "2026-03-25T00:00:00",
    "endDate": "2026-04-01T00:00:00",
    "categories": [
      { "categoryId": 1, "amount": 50000 },
      { "categoryId": 2, "amount": 30000 }
    ]
  }
}
```

---

### 팀 참여 (초대코드)
```
POST /api/teams/v1/join
Authorization: Bearer {token}
```

**Request Body**
```json
{
  "inviteCode": "AB3K9P"
}
```

**Response**
```json
{
  "status": 201,
  "message": "생성 완료",
  "data": {
    "teamId": 1,
    "teamName": "절약왕팀",
    "currentMemberCount": 2,
    "maxMemberCount": 4,
    "challengeId": 1,
    "startDate": "2026-03-25T00:00:00",
    "endDate": "2026-04-01T00:00:00"
  }
}
```

---

## 2. 챌린지 생성 흐름

```
로그인 유저
    ↓
[유효성 검증]
  - memberCount에 해당하는 최소 목표 금액 정책 조회
  - goalAmount >= policy.minAmount 확인
    ↓
[초대코드 생성]
  - 6자리 랜덤 코드 생성 (중복 시 재생성)
    ↓
[단일 트랜잭션 내 저장]
  1. Team 저장 (팀장이 설정한 maxMemberCount 포함)
  2. TeamMembers 저장 (생성자 → LEADER)
  3. Challenge 저장 (시작일~+7일, 상태: WAITING)
  4. ChallengeWeek 저장 (1주차, 목표금액 = 챌린지 전체 목표금액)
  5. ChallengeCategory 저장 (선택한 카테고리별 기준 금액)
    ↓
CreateChallengeResponse 반환 (초대코드 포함)
```

### 비즈니스 규칙
| 항목 | 규칙 |
|---|---|
| 팀 인원 | 최소 2명, 최대 8명 (DTO `@Min/@Max` 검증) |
| 카테고리 | 최소 1개, 최대 3개 (DTO `@Size` 검증) |
| 챌린지 기간 | 시작일로부터 정확히 7일 |
| 목표 금액 | 인원수별 최소 금액 정책(`ChallengeMinGoalPolicy`) 충족 필요 |
| 인당 최소 목표 금액 | 5,000원 이상, 300,000원 이하 (DTO `@Min/@Max` 검증) |
| 팀장 | 챌린지 생성자가 자동으로 `LEADER` 등록 |

---

## 3. 팀 참여 흐름

```
로그인 유저 + 초대코드 입력
    ↓
초대코드로 Team 조회 → 없으면 TEAM_NOT_FOUND
    ↓
해당 팀의 WAITING 상태 Challenge 조회 → 없으면 CHALLENGE_NOT_JOINABLE
    ↓
이미 팀원인지 확인 → ALREADY_TEAM_MEMBER
    ↓
정원 초과 확인 (currentMemberCount >= maxMemberCount) → TEAM_ALREADY_FULL
    ↓
TeamMembers 저장 (MEMBER 역할)
team.increaseMemberCount()
    ↓
JoinTeamResponse 반환
```

### QR코드 초대 방식 (향후 확장)
QR코드 이미지 생성은 iOS(CoreImage)에서 처리하며, 백엔드는 초대코드 기반 팀 참여 API만으로 충분합니다.

```
백엔드: 초대코드 발급 → iOS: 딥링크(jjanpot://join?code=AB3K9P)로 QR 생성
```

---

## 4. 엔티티 설계 결정사항

### nullable=false 컬럼 → primitive 타입 사용
DB에서 null을 허용하지 않는 컬럼은 Java에서도 null 가능성을 없애기 위해 primitive 타입 사용.
단, `@Id` 필드는 Hibernate가 새 엔티티 판별에 null을 사용하므로 `Long` (wrapper) 유지.

| 엔티티 | 변경 필드 |
|---|---|
| `Team` | `currentMemberCount`, `maxMemberCount`: `Integer` → `int` |
| `Challenge` | `goalAmount`, `minPersonalGoalAmount`: `Long` → `int` |
| `ChallengeWeek` | `weekNumber`, `weekGoalAmount`, `weekSavedAmount`: `Integer/Long` → `int` |
| `ChallengeMinGoalPolicy` | `memberCount`, `minAmount`: `Integer/Long` → `int` |
| `ChallengeCategory` | `amount`: `Long` → `int` |

### Team 엔티티에서 minMemberCount 제거
- `minMemberCount`는 항상 2로 고정된 서비스 정책
- DTO의 `@Min(2)` 검증으로 충분하므로 DB 컬럼 불필요

### 정적 팩토리 메서드 도입
Service에서 `builder()` 직접 사용 대신 엔티티에 정적 팩토리 메서드를 두어 생성 책임을 엔티티에 위임.

| 엔티티 | 팩토리 메서드 |
|---|---|
| `Team` | `Team.of(teamName, inviteCode, type, maxMemberCount)` |
| `TeamMembers` | `TeamMembers.ofLeader(team, user)`, `TeamMembers.ofMember(team, user)` |
| `Challenge` | `Challenge.from(request, team, startDateTime, endDateTime)` |
| `ChallengeWeek` | `ChallengeWeek.firstWeek(challenge, startDateTime, endDateTime)` |
| `ChallengeCategory` | `ChallengeCategory.of(challenge, category, amount)` |

---

## 5. 초대코드 설계

### 생성 방식
```java
private static final String INVITE_CODE_CHARS = "ABCDEFGHJKMNPQRSTUVWXYZ23456789";
// O, I, L, 0, 1 제외 → 육안으로 혼동 가능한 문자 배제
private static final int INVITE_CODE_LENGTH = 6;
```

| 항목 | 내용 |
|---|---|
| 길이 | 6자리 |
| 문자셋 | A-Z, 2-9 중 혼동 문자 제외 (32가지) |
| 경우의 수 | 32⁶ ≈ 10억 |
| 생성 방식 | `SecureRandom` 기반 랜덤 생성 |
| 중복 처리 | DB unique 조회 후 중복 시 재생성 |

### UUID 방식 대비 개선점
- UUID는 16진수(0-9, A-F)만 사용 → 16⁸ ≈ 43억이지만 코드가 어색함 (`A3F90B1C`)
- 32가지 문자 × 6자리로 사용자가 읽고 입력하기 편한 코드 생성

---

## 6. Security 설정

### 인증 경로 설정
```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/auth/**").permitAll()  // 로그인은 인증 불필요
    .anyRequest().authenticated()                 // 나머지 전체 인증 필요
)
.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
```

### 수정 전 문제
- `JwtAuthenticationFilter`가 필터 체인에 등록되지 않아 JWT 인증이 동작하지 않음
- `authorizeHttpRequests` 미설정으로 모든 요청이 인증 없이 통과

---

## 7. 예외 처리

이번 작업에서 추가된 ErrorCode

| ErrorCode | HTTP Status | 메시지 |
|---|---|---|
| `CHALLENGE_NOT_JOINABLE` | 400 | 참여 가능한 챌린지가 없습니다. 이미 시작되었거나 종료된 챌린지입니다. |
| `CHALLENGE_MIN_GOAL_POLICY_NOT_FOUND` | 404 | 최소 목표 금액 정책을 찾을 수 없습니다. |
| `GOAL_AMOUNT_BELOW_MINIMUM` | 400 | 팀 전체 목표 금액이 최소 기준에 미달합니다. |
| `CATEGORY_NOT_FOUND` | 404 | 카테고리를 찾을 수 없습니다. |
| `TEAM_NOT_FOUND` | 404 | 팀을 찾을 수 없습니다. |
| `TEAM_ALREADY_FULL` | 400 | 팀 정원이 초과되었습니다. |
| `ALREADY_TEAM_MEMBER` | 400 | 이미 팀에 참여한 사용자입니다. |

---

## 8. 테스트

### ChallengeServiceTest (단위 테스트)
- 방식: `@ExtendWith(MockitoExtension.class)` — Spring 컨텍스트 없이 Mockito로 격리
- 위치: `src/test/java/.../challenge/service/ChallengeServiceTest.java`

| 테스트 케이스 | 검증 내용 |
|---|---|
| 카테고리 1개 성공 | 응답 값 (`challengeId`, `teamName`, `inviteCode`, `categories`) 정상 여부 |
| 카테고리 3개 성공 | 최대 선택 케이스 |
| 초대코드 중복 재생성 | `existsByInviteCode` 3번 호출 (`verify`) |
| 저장 호출 검증 | 5개 save 메서드 각 1회 호출 (`verify`) |
| 실패 - 정책 없음 | `CHALLENGE_MIN_GOAL_POLICY_NOT_FOUND` 예외 + save 미호출 |
| 실패 - 목표금액 미달 | `GOAL_AMOUNT_BELOW_MINIMUM` 예외 |
| 실패 - 카테고리 없음 | `CATEGORY_NOT_FOUND` 예외 + `saveAll` 미호출 |

### 의존성 추가
```groovy
testImplementation 'org.springframework.boot:spring-boot-starter-test'
// JUnit 5, Mockito, AssertJ 포함
```

---

## 9. 패키지 구조

```
domain/
├── challenge/
│   ├── controller/
│   │   ├── ChallengeControllerV1.java
│   │   └── docs/ChallengeControllerV1Docs.java
│   ├── dto/
│   │   ├── ChallengeCategoryRequest.java
│   │   ├── CreateChallengeRequest.java
│   │   └── CreateChallengeResponse.java
│   ├── entity/
│   │   ├── Challenge.java
│   │   ├── ChallengeCategory.java
│   │   ├── ChallengeCategoryId.java
│   │   ├── ChallengeMinGoalPolicy.java
│   │   ├── ChallengeStatus.java
│   │   └── ChallengeWeek.java
│   ├── repository/
│   │   ├── ChallengeRepository.java
│   │   ├── ChallengeCategoryRepository.java
│   │   ├── ChallengeMinGoalPolicyRepository.java
│   │   └── ChallengeWeekRepository.java
│   └── service/
│       └── ChallengeService.java
│
└── team/
    ├── controller/
    │   ├── TeamControllerV1.java
    │   └── docs/TeamControllerV1Docs.java
    ├── dto/
    │   ├── JoinTeamRequest.java
    │   └── JoinTeamResponse.java
    ├── entity/
    │   ├── Team.java
    │   ├── TeamMembers.java
    │   ├── TeamMembersId.java
    │   ├── TeamRole.java
    │   └── TeamType.java
    ├── repository/
    │   ├── TeamRepository.java
    │   └── TeamMembersRepository.java
    └── service/
        └── TeamService.java
```

# 백엔드 역할 분배

## MVP 기능 기준 역할 분배 (4인)

---

### 나 (챌린지/팀/홈)

**담당 도메인**: `team`, `challenge`, `category`, `user` (온보딩)

| API | 설명 |
|-----|------|
| `POST /teams` | 팀 + 챌린지 + challenge_week 동시 생성 |
| `GET /teams/{id}/invite-code` | 초대코드 조회 |
| `POST /teams/join` | 초대코드 입력 → 팀 참여 |
| `GET /categories` | 카테고리 목록 + 금액 선택지 조회 |
| `GET /challenges/{id}/dashboard` | 홈 대시보드 (개인 절약 현황 + 팀 목표 달성률 + 팀원 활동) |
| `PATCH /users/me/profile` | 프로필 설정 (온보딩) |

> 카카오 로그인 완성 + `user_agreement` 약관 동의도 포함

---

### 팀원1 (절약 인증 피드)

**담당 도메인**: `certification`, `certification_like`

| API | 설명 |
|-----|------|
| `POST /certifications` | 인증 작성 (S3 이미지 업로드 포함) |
| `GET /certifications` | 팀 피드 조회 (challengeId 기준 페이징) |
| `DELETE /certifications/{id}` | 인증 삭제 → `week_saved_amount` 차감 트랜잭션 |
| `POST /certifications/{id}/likes` | 좋아요 (soft delete 방식, LIKE 알림 트리거) |
| `DELETE /certifications/{id}/likes` | 좋아요 취소 |

> 팀원3과 협업 필요: 좋아요 생성 시 LIKE 알림 발송 → 이벤트 발행 or 직접 호출 방식 사전 합의 필요

---

### 팀원2 (챌린지 결과리포트 / 마이페이지)

**담당 도메인**: `challenge_team_result`, `challenge_member_result`, `item`

| API | 설명 |
|-----|------|
| `GET /challenges/{id}/result` | 챌린지 결과 조회 (개인 + 팀 성과) |
| `GET /users/me/challenges` | 나의 챌린지 히스토리 |
| `GET /challenges/{id}/result/share` | 결과 이미지 생성 (SNS 공유용) |
| Scheduler | 매일 자정: 종료된 챌린지 → 개인/팀 성공/실패 판단 → result 저장 → `challenge.status` 변경 |

> 핵심 구현: Spring Scheduler로 챌린지 종료 흐름 전체 담당. `challenge_min_goal_policy` 기준 로직도 포함

---

### 팀원3 (알림)

**담당 도메인**: `notification`, `notification_template`, `user_device`

| API | 설명 |
|-----|------|
| `POST /devices` | FCM 토큰 등록/갱신 (로그인 시 호출) |
| `GET /notifications` | 알림 목록 조회 |
| `PATCH /notifications/{id}/read` | 읽음 처리 |
| Scheduler (18시) | 당일 인증 없는 유저 → ENCOURAGE FCM 발송 |
| Scheduler (20시) | 3/5/7일차 + 주간 인증 0회 유저 → ENCOURAGE FCM 발송 |
| LIKE 알림 수신 | 팀원1에서 좋아요 발생 시 트리거 |
| GOAL_NEAR / GOAL_COMPLETE | 팀원2 Scheduler와 연동 |

---

## 도메인 간 의존성 / 협업 포인트

```
나         → 팀원1: Challenge, Team 정보 (피드에서 challengeId 필요)
팀원1      → 팀원3: 좋아요 발생 시 LIKE 알림 발송
팀원2      → 팀원3: 챌린지 종료 시 GOAL_COMPLETE 알림 발송
팀원3      → 나:    FCM 토큰 등록 시점 (로그인 플로우와 연동)
```

---

## 구현 완료 현황 (feat/SAT-9 기준)

| 기능 | 상태 |
|---|---|
| 소셜 로그인 (카카오/구글/애플) | ✅ |
| 챌린지 생성 + 팀 생성 + 초대코드 발급 | ✅ |
| 초대코드로 팀 참여 | ✅ |
| JWT 필터 등록 + 인증 경로 설정 | ✅ |
| 챌린지/팀/카테고리 Repository | ✅ |
| 챌린지 생성 단위 테스트 (7개) | ✅ |

---

## 팀원3 분량 보완 권장

알림 기능 단독으로는 분량이 상대적으로 적으므로 **챌린지 상태 자동 전환 스케줄러**를 함께 담당하는 것을 권장합니다.

```
WAITING     → IN_PROGRESS  (시작일 00:00 자동 전환)
IN_PROGRESS → FINISHED     (종료일 00:00 자동 전환 + 결과 집계 트리거)
```

이 스케줄러가 있어야 팀원2의 결과 리포트 API가 정상 동작하며, 알림 발송 타이밍과도 자연스럽게 연결됩니다.

---

## 팀원1 주의사항

절약 인증에 **사진 업로드(S3 연동)** 가 포함되어 있어 분량이 생각보다 많을 수 있습니다. 일정 초반에 S3 업로드 공통 유틸 구현을 우선 진행하는 것을 권장합니다.

---

## 미확인 / 사전 합의 필요 항목

- `auth` (카카오 로그인) 현재 구현 범위 확인 → 완성 담당자 명확히 지정
- S3 업로드 모듈 → 팀원1 주도, 공통 유틸로 분리 권장
- `challenge_min_goal_policy` 초기 데이터 삽입 → DB 마이그레이션 스크립트로 처리
- 좋아요 → 알림 연동 방식 (Spring Event vs 직접 호출) → 팀원1 + 팀원3 합의
- 챌린지 상태 자동 전환 스케줄러 담당자 확정 (팀원2 or 팀원3)
- 팀원3 알림 기능과 스케줄러 분량 조율 후 최종 분배 확정

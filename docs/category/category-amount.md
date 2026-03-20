# 카테고리 금액 관련 컬럼 역할 정리

---

## 컬럼별 역할

### category.default_amount
- **용도**: 물건 환산 시 기준값으로만 사용
- **예시**: `week_saved_amount = 45,000원` → 카페/디저트 `default_amount = 4,500원` → "아메리카노 10잔 ☕"
- **챌린지 생성 시 사용 안 함**

### category_amount_option.amount
- **용도**: 챌린지 생성 시 카테고리별 금액 선택지 표시
- **예시**: 카페/디저트 선택 → 1,500원 / 2,000원 / 4,000원 / 7,000원 / 직접입력
- **저장 안 됨**: 선택지 표시용이며 최종값은 `challenge_category.amount`에 저장

### challenge_category.amount
- **용도 1**: 팀장이 챌린지 생성 시 확정한 카테고리별 기준금액 저장
- **용도 2**: `saved_amount` 계산 기준값
- 선택지 값이든 직접 입력값이든 최종 확정된 값만 저장됨

---

## saved_amount 계산식

### 지출 시
```
saved_amount = challenge_category.amount - spend_amount
```

### 무지출 시
```
spend_amount = 0
saved_amount = challenge_category.amount
```

### 기준 초과 소비 시 (음수 가능)
```
카테고리 기준금액 = 4,500원
실제 사용 금액   = 6,000원
saved_amount     = 4,500 - 6,000 = -1,500원
```

> `saved_amount`는 음수가 될 수 있으므로 `ChallengeWeek.addSavedAmount()`에서 음수 검증 없이 그대로 누적합니다.

---

## 챌린지 생성 흐름

```
카테고리 선택 (카페/디저트)
  → category_amount_option 조회
  → 선택지 표시: 1,500 / 2,000 / 4,000 / 7,000 / 직접입력
  → 팀장이 선택 또는 직접 입력
  → challenge_category.amount에 최종값 저장
```

---

## 물건 환산 흐름

```
week_saved_amount (팀 합산 절약 금액)
  → category.default_amount 기준으로 item 테이블에서 물건 조회
  → "아메리카노 10잔 ☕" 형태로 환산
```

---

## 테이블 연관관계

```
category (1)
  └─(1:N)─ category_amount_option  → 챌린지 생성 시 선택지 표시용
  └─(1:N)─ item                    → 물건 환산용 (default_amount 기준)

challenge_category.amount
  → category_amount_option와 직접 연관 없음
  → 팀장이 선택/입력한 최종 확정값만 저장
```

---

## 요약 비교표

| 컬럼 | 저장 여부 | 사용 시점 | 비고 |
|---|---|---|---|
| `category.default_amount` | 고정값 | 결과 화면 물건 환산 | 챌린지 생성과 무관 |
| `category_amount_option.amount` | 저장 안 됨 | 챌린지 생성 시 선택지 표시 | UI 표시용 |
| `challenge_category.amount` | 저장됨 | 인증 시 saved_amount 계산 기준 | 팀장이 확정한 값 |

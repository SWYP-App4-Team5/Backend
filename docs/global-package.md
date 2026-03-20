# Global 패키지 구조 및 설명

> `com.swyp.server.global` 패키지는 애플리케이션 전반에 걸쳐 공통으로 사용되는 설정, 예외 처리, 로깅, 보안, API 문서화 모듈을 포함합니다.

---

## 목차

- [패키지 구조](#패키지-구조)
- [AOP 모듈](#aop-모듈)
- [Common 모듈](#common-모듈)
- [Entity 모듈](#entity-모듈)
- [Exception 모듈](#exception-모듈)
- [Security 모듈](#security-모듈)
- [Swagger 모듈](#swagger-모듈)
- [전체 요청 흐름](#전체-요청-흐름)

---

## 패키지 구조

```
com/swyp/server/global/
├── aop/
│   ├── MdcTraceId.java
│   ├── TraceIdAspect.java
│   └── ServiceExceptionLoggingAspect.java
├── common/
│   ├── config/
│   │   └── JpaConfig.java
│   └── dto/
│       ├── SuccessResponse.java
│       └── ErrorResponse.java
├── entity/
│   └── BaseEntity.java
├── exception/
│   ├── ErrorCode.java
│   ├── BusinessException.java
│   └── GlobalExceptionHandler.java
├── security/
│   └── config/
│       └── SecurityConfig.java
└── swagger/
    ├── SwaggerConfig.java
    └── annotation/
        ├── ApiErrorCodeExample.java
        ├── ApiErrorCodeExamples.java
        └── ExampleHolder.java
```

---

## AOP 모듈

> 요청 추적(Tracing)과 예외 로깅을 횡단 관심사로 분리하여 처리합니다.

### MdcTraceId.java

SLF4J MDC(Mapped Diagnostic Context)에 traceId를 관리하는 유틸리티 클래스입니다.

| 메서드 | 설명 |
|--------|------|
| `generate()` | UUID 앞 8자리로 고유 traceId 생성 |
| `put(String)` | MDC에 traceId 저장 |
| `get()` | 현재 traceId 조회 |
| `remove()` | MDC에서 traceId 제거 |
| `isPresent()` | traceId 존재 여부 확인 |
| `putIfAbsent()` | traceId가 없을 때만 생성 및 저장 |

### TraceIdAspect.java

- **어노테이션**: `@Aspect`, `@Component`, `@Order(1)`
- **포인트컷**: Controller, Service 계층의 모든 메서드
- **동작**: `@Around`로 메서드 실행을 감싸 traceId 생성 → 메서드 실행 → traceId 정리

### ServiceExceptionLoggingAspect.java

- **어노테이션**: `@Aspect`, `@Component`, `@Order(2)`
- **포인트컷**: Service 계층의 모든 메서드
- **동작**: `@AfterThrowing`으로 예외 발생 시 로그 기록
- `BusinessException`은 `GlobalExceptionHandler`에서 처리하므로 필터링

**실행 순서**: `TraceIdAspect(1)` → `ServiceExceptionLoggingAspect(2)`

---

## Common 모듈

### JpaConfig.java

```java
@Configuration
@EnableJpaAuditing
public class JpaConfig { }
```

JPA Auditing을 활성화하여 엔티티의 `createdAt`, `updatedAt`을 자동 관리합니다.

---

### SuccessResponse\<T\>

API 성공 응답을 감싸는 공통 래퍼 클래스입니다.

```java
// 데이터 + 메시지
SuccessResponse.of(data, message)

// 데이터만
SuccessResponse.of(data)

// 메시지만
SuccessResponse.ofMessage(message)
```

- `@JsonInclude(NON_NULL)` — null 필드는 JSON 직렬화에서 제외

### ErrorResponse

API 에러 응답의 표준 구조입니다.

```java
// 메시지만
ErrorResponse.of(message)

// 필드 검증 오류 포함
ErrorResponse.of(message, List<FieldError>)
```

| 필드 | 설명 |
|------|------|
| `traceId` | MDC에서 자동 주입 (`MdcTraceId.get()`) |
| `message` | 에러 메시지 |
| `errors` | 필드 검증 오류 목록 (`List<FieldError>`) |

**내부 클래스 FieldError**

| 필드 | 설명 |
|------|------|
| `field` | 검증 실패 필드명 |
| `value` | 입력된 값 |
| `reason` | 실패 사유 |

---

## Entity 모듈

### BaseEntity.java

모든 엔티티가 상속하는 추상 기반 클래스입니다.

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @CreatedDate
    private LocalDateTime createdAt;  // 생성 시각 (불변)

    @LastModifiedDate
    private LocalDateTime updatedAt;  // 수정 시각 (자동 갱신)
}
```

---

## Exception 모듈

### ErrorCode.java

비즈니스 에러 코드를 열거형으로 관리합니다.

| 코드 | HTTP 상태 | 메시지 |
|------|-----------|--------|
| `INVALID_INPUT` | 400 | 입력값이 올바르지 않습니다. |
| `UNAUTHORIZED` | 401 | 인증이 필요합니다. |
| `FORBIDDEN` | 403 | 권한이 없습니다. |
| `NOT_FOUND` | 404 | 요청한 리소스를 찾을 수 없습니다. |
| `INTERNAL_SERVER_ERROR` | 500 | 서버 내부 오류가 발생했습니다. |
| `HTTP_REQUEST_CONTEXT_NOT_FOUND` | 500 | HTTP 요청 컨텍스트를 찾을 수 없습니다. |

### BusinessException.java

애플리케이션 비즈니스 로직에서 발생하는 커스텀 예외입니다.

```java
// ErrorCode 기본 메시지 사용
throw new BusinessException(ErrorCode.NOT_FOUND);

// 커스텀 메시지 사용
throw new BusinessException(ErrorCode.NOT_FOUND, "해당 사용자를 찾을 수 없습니다.");
```

- `RuntimeException` 상속 (Unchecked Exception)

### GlobalExceptionHandler.java

`@RestControllerAdvice`로 등록된 전역 예외 처리기입니다.

| 핸들러 | 대상 예외 | 동작 |
|--------|----------|------|
| `handleBusinessException` | `BusinessException` | 4xx → WARN, 5xx → ERROR 로깅 후 `ErrorCode`의 상태 코드 반환 |
| `handleIllegalArgumentException` | `IllegalArgumentException` | ERROR 로깅, 400 반환 |
| `handleMethodArgumentNotValid` | 검증 실패 (`@Valid`) | FieldError 목록과 함께 400 반환 |
| `handleUnexpectedException` | 그 외 모든 예외 | ERROR 로깅, 500 반환 |

---

## Security 모듈

### SecurityConfig.java

Spring Security 필터 체인을 용도에 따라 분리하여 구성합니다.

| 빈 | Order | 적용 경로 | 처리 방식 |
|----|-------|----------|----------|
| `filterChainSwagger` | 1 | `/swagger-ui/**`, `/v3/api-docs/**` 등 | 인증 없이 전체 허용 |
| `filterChainApi` | 2 | 나머지 모든 API | Stateless, CSRF 비활성화 |

> **참고**: `filterChainApi`의 인가 규칙은 인증 기능 구현 시 추가 예정입니다.

---

## Swagger 모듈

### SwaggerConfig.java

OpenAPI 3.0 문서를 구성하고, 커스텀 어노테이션을 처리하는 `OperationCustomizer`를 등록합니다.

- JWT Bearer 인증 스키마 등록
- API 서버 주소: `http://localhost:8080`
- `@ApiErrorCodeExample` / `@ApiErrorCodeExamples` 어노테이션 감지 → 에러 응답 예시 자동 생성

### 커스텀 어노테이션

#### @ApiErrorCodeExample

단일 ErrorCode 타입에 대한 에러 예시를 문서화합니다.

```java
@ApiErrorCodeExample(
    value = ErrorCode.class,
    include = {"NOT_FOUND", "FORBIDDEN"},  // 생략 시 전체 포함
    isValidationError = false              // FieldError 예시 포함 여부
)
```

#### @ApiErrorCodeExamples

여러 ErrorCode 타입을 한 번에 문서화합니다.

```java
@ApiErrorCodeExamples(
    value = {ErrorCode.class, OtherErrorCode.class},
    include = {"NOT_FOUND"},
    isValidationError = false
)
```

#### ExampleHolder.java

Swagger 에러 예시 생성 과정에서 데이터를 임시로 담는 내부 홀더 클래스입니다.

| 필드 | 설명 |
|------|------|
| `holder` | Swagger Example 객체 |
| `name` | 에러 코드명 |
| `code` | HTTP 상태 코드 |

---

## 전체 요청 흐름

```
HTTP 요청 수신
      │
      ▼
[TraceIdAspect @Order(1)]
  MDC에 traceId 생성 및 주입
      │
      ▼
Controller / Service 실행
      │
      ├─ 정상 처리 ──────────────────────────► SuccessResponse 반환
      │
      └─ 예외 발생
            │
            ▼
   [ServiceExceptionLoggingAspect @Order(2)]
     비즈니스 외 예외 로그 기록
            │
            ▼
   [GlobalExceptionHandler]
     예외 종류에 따라 분기 처리
     traceId가 포함된 ErrorResponse 반환
            │
            ▼
   [TraceIdAspect]
     MDC에서 traceId 제거 (정리)
```

> 모든 로그와 에러 응답에 동일한 `traceId`가 포함되므로, 장애 발생 시 로그와 응답을 traceId로 연결하여 빠른 디버깅이 가능합니다.

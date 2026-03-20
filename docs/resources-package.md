# Resources 패키지 구조 및 설명

> `src/main/resources` 디렉토리는 애플리케이션 설정, DB 마이그레이션, 로그 설정 파일을 포함합니다.

---

## 목차

- [디렉토리 구조](#디렉토리-구조)
- [application.yml](#applicationyml)
- [config/flyway.yml](#configflywayyml)
- [db/migration/V1__init_schema.sql](#dbmigrationv1__init_schemasql)
- [logback-spring.xml](#logback-springxml)
- [설정 간 연관 관계](#설정-간-연관-관계)

---

## 디렉토리 구조

```
src/main/resources/
├── application.yml              # 메인 애플리케이션 설정
├── config/
│   └── flyway.yml               # Flyway DB 마이그레이션 설정
├── db/
│   └── migration/
│       └── V1__init_schema.sql  # 초기 DB 스키마 정의
└── logback-spring.xml           # 환경별 로그 설정
```

---

## application.yml

애플리케이션의 최상위 설정 파일입니다.

```yaml
spring:
  application:
    name: server
  profiles:
    default: local
  config:
    import:
      - optional:config/flyway.yml
```

| 항목 | 값 | 설명 |
|------|-----|------|
| `application.name` | `server` | 애플리케이션 식별 이름 |
| `profiles.default` | `local` | 프로파일 미지정 시 기본값 |
| `config.import` | `optional:config/flyway.yml` | flyway.yml 선택적 임포트 |

**`optional:` 접두사**: 해당 파일이 존재하지 않아도 애플리케이션이 정상 기동됩니다. 파일 누락으로 인한 기동 실패를 방지하는 안전장치입니다.

---

## config/flyway.yml

Flyway DB 마이그레이션 동작을 제어하는 설정 파일입니다.
`application.yml`의 `config.import`를 통해 선택적으로 로드됩니다.

```yaml
spring:
  flyway:
    enabled: false
    baseline-on-migrate: true
    baseline-version: 0
    clean-disabled: true
```

| 항목 | 값 | 설명 |
|------|-----|------|
| `enabled` | `false` | Flyway 자동 실행 비활성화 (현재 비활성 상태) |
| `baseline-on-migrate` | `true` | 기존 DB에 최초 마이그레이션 실행 시 baseline 자동 생성 |
| `baseline-version` | `0` | baseline 시작 버전 번호 |
| `clean-disabled` | `true` | `flyway:clean` 명령 실행 금지 (DB 전체 삭제 방지) |

> **`clean-disabled: true`**: 운영 환경에서 실수로 DB 전체를 초기화하는 사고를 방지하기 위한 필수 설정입니다.

---

## db/migration/V1__init_schema.sql

Flyway가 관리하는 첫 번째 DB 마이그레이션 스크립트입니다.

**파일 네이밍 규칙**: `V{버전}__{설명}.sql`

| 구성 요소 | 값 | 설명 |
|----------|-----|------|
| `V` | 버전 접두사 | Flyway 버전 마이그레이션 파일 식별자 |
| `1` | 버전 번호 | 실행 순서를 결정 |
| `init_schema` | 설명 | 해당 마이그레이션의 목적 |

> **현재 상태**: 파일은 생성되어 있으나 내용이 비어 있습니다. Flyway가 활성화(`enabled: true`)되면 이 파일에 정의된 SQL이 실행되어 초기 테이블 구조가 생성됩니다.

---

## logback-spring.xml

Spring Boot와 통합된 Logback 설정 파일입니다.
`-spring` 접미사 덕분에 `springProfile` 태그를 통해 **활성 프로파일에 따라 로그 설정을 분기**할 수 있습니다.

### 로그 패턴

```
%d{HH:mm:ss.SSS}               → 시각
%highlight(%-5level)            → 로그 레벨 (색상 하이라이트)
%magenta([%15.15thread])        → 스레드명 (자홍색, 15자 고정폭)
%yellow([%X{traceId:-no-id}])   → MDC traceId (없으면 "no-id", 노란색)
%cyan(%-40.40logger{39})        → 로거명 (청록색, 40자 고정폭)
: %msg%n                        → 로그 메시지
```

> `%X{traceId}` — `TraceIdAspect`가 MDC에 주입한 traceId를 로그에 자동으로 포함합니다. 요청별 로그 추적이 가능해집니다.

---

### Appender 구성

| Appender | 클래스 | 설명 |
|----------|--------|------|
| `SERVER_CONSOLE` | `ConsoleAppender` | 콘솔에 동기 출력 |
| `ASYNC_SERVER_CONSOLE` | `AsyncAppender` | 비동기 큐를 통해 콘솔 출력 |

**AsyncAppender 설정값**

| 속성 | 값 | 설명 |
|------|-----|------|
| `discardingThreshold` | `0` | 큐가 꽉 차도 로그를 버리지 않음 |
| `neverBlock` | `true` | 로그 처리가 느려도 애플리케이션 스레드를 블로킹하지 않음 |
| `queueSize` | `512` | 비동기 처리 대기 큐 크기 |
| `includeCallerData` | `true` | 호출 위치(파일명, 라인 번호) 정보 포함 |

---

### 환경(Profile)별 로그 레벨

#### local

로컬 개발 환경으로, SQL 및 애플리케이션 디버그 로그를 상세히 출력합니다.

```xml
<logger level="DEBUG" name="swyp"/>              <!-- 애플리케이션 패키지 -->
<logger level="DEBUG" name="jdbc.sqltiming"/>    <!-- SQL 실행 시간 -->
<logger level="DEBUG" name="jdbc.resultsettable"/> <!-- 쿼리 결과 테이블 -->
<root level="INFO"/>
```

> `name="swyp"`는 추후 팀명으로 변경 예정입니다. (파일 내 TODO 주석 확인)

#### dev, stg

개발/스테이징 환경으로, 불필요한 JDBC 로그는 끄고 핵심 SQL 정보만 출력합니다.

```xml
<logger level="DEBUG" name="jdbc.sqltiming"/>      <!-- SQL 실행 시간 -->
<logger level="DEBUG" name="jdbc.resultsettable"/> <!-- 쿼리 결과 테이블 -->
<logger level="OFF"   name="jdbc.sqlonly"/>        <!-- SQL 단독 로그 끔 -->
<logger level="OFF"   name="jdbc.audit"/>          <!-- JDBC 감사 로그 끔 -->
<logger level="OFF"   name="jdbc.resultset"/>      <!-- ResultSet 로그 끔 -->
<logger level="OFF"   name="jdbc.connection"/>     <!-- 커넥션 로그 끔 -->
<logger level="WARN"  name="io.lettuce.core.protocol"/> <!-- Redis 프로토콜 경고만 -->
<root level="INFO"/>
```

#### prod

운영 환경으로, 최소한의 로그만 출력하여 성능 영향을 줄입니다.

```xml
<logger level="INFO"  name="org.springframework"/> <!-- Spring 프레임워크 INFO -->
<logger level="WARN"  name="org.hibernate.SQL"/>   <!-- Hibernate SQL은 경고만 -->
<root level="INFO"/>
```

---

## 설정 간 연관 관계

```
application.yml
    │
    ├── profiles.default=local ──────────► logback-spring.xml (local 프로파일 적용)
    │
    └── config.import ───────────────────► config/flyway.yml
                                                │
                                                └── enabled=false
                                                    (활성화 시 실행)
                                                         │
                                                         ▼
                                                db/migration/V1__init_schema.sql
```

### traceId 연동 흐름

```
HTTP 요청 수신
    │
    ▼
[TraceIdAspect]
  MDC.put("traceId", UUID)
    │
    ▼
비즈니스 로직 실행
    │
    ├── 로그 출력 ──► logback-spring.xml의 %X{traceId} 로 traceId 자동 포함
    │
    └── 예외 발생 ──► ErrorResponse.traceId = MdcTraceId.get()
                      응답 본문에 traceId 포함
    │
    ▼
[TraceIdAspect]
  MDC.remove("traceId")
```

> 로그 파일과 API 에러 응답에 동일한 `traceId`가 포함되므로, 장애 발생 시 응답의 traceId로 해당 요청의 전체 로그를 즉시 추적할 수 있습니다.

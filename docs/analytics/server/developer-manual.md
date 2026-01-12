# MVP Server 개발자 매뉴얼

## 문서 정보

- **버전**: 1.0.0
- **최종 수정일**: 2026-01-09
- **프로젝트**: LLM 관리 시스템 - 백엔드 서버

## 목차

1. [프로젝트 개요](#프로젝트-개요)
2. [기술 스택](#기술-스택)
3. [프로젝트 구조](#프로젝트-구조)
4. [개발 환경 설정](#개발-환경-설정)
5. [빌드 및 실행 방법](#빌드-및-실행-방법)
6. [엔티티 구조 및 관계](#엔티티-구조-및-관계)
7. [비즈니스 로직 설계](#비즈니스-로직-설계)
8. [API 엔드포인트 목록](#api-엔드포인트-목록)
9. [테스트 방법](#테스트-방법)
10. [코드 스타일 및 컨벤션](#코드-스타일-및-컨벤션)
11. [개발 시 주의사항](#개발-시-주의사항)

---

## 프로젝트 개요

### 시스템 개요

MVP Server는 LLM(Large Language Model) 모델 관리 및 팀 할당량 관리를 위한 Spring Boot 백엔드 서버입니다. 이 시스템은 다음과 같은 기능을 제공합니다:

- 사용자 인증 및 권한 관리 (JWT 기반)
- LLM 모델 등록 및 관리
- 팀 생성 및 멤버 관리
- 팀별 할당량(Quota) 설정 및 관리
- 사용량(Usage) 추적 및 비용 계산
- 대시보드 통계 데이터 제공

### 아키텍처 특징

- **RESTful API**: 표준 REST 아키텍처 준수
- **계층형 아키텍처**: Controller → Service → Repository → Entity
- **JWT 인증**: Stateless한 토큰 기반 인증
- **JPA ORM**: Hibernate를 활용한 데이터베이스 추상화

---

## 기술 스택

### 핵심 기술

| 기술 | 버전 | 용도 |
|------|------|------|
| Java | 17 | 프로그래밍 언어 |
| Spring Boot | 3.2.0 | 애플리케이션 프레임워크 |
| Gradle | 8.x | 빌드 도구 |
| Spring Data JPA | 3.2.0 | ORM 및 데이터 접근 |
| Spring Security | 6.2.0 | 보안 및 인증 |
| H2 Database | 2.x | 인메모리 데이터베이스 (개발용) |

### 추가 라이브러리

| 라이브러리 | 버전 | 용도 |
|-----------|------|------|
| SpringDoc OpenAPI | 2.2.0 | API 문서화 (Swagger) |
| JWT (jjwt) | 0.11.5 | 토큰 생성 및 검증 |
| Lombok | Latest | 코드 줄이기 및 편의 기능 |
| Jakarta Validation | 3.x | 데이터 유효성 검사 |

---

## 프로젝트 구조

### 디렉토리 구조

```
mvp-server/
├── src/main/java/com/example/llm/
│   ├── config/           # 설정 클래스 (Security, Swagger, Exception Handler)
│   ├── controller/       # REST API 컨트롤러
│   ├── dto/             # Data Transfer Objects (Request/Response)
│   ├── entity/          # JPA 엔티티
│   ├── exception/       # 커스텀 예외
│   ├── repository/      # JPA 리포지토리
│   ├── service/         # 비즈니스 로직
│   └── util/           # 유틸리티 클래스
├── src/main/resources/
│   ├── application.properties  # 설정 파일
│   └── data.sql            # 초기 데이터
├── build.gradle           # Gradle 빌드 설정
└── README.md              # 프로젝트 개요
```

### 패키지 구성

#### [`config/`](../mvp-server/src/main/java/com/example/llm/config/)
- [`SecurityConfig.java`](../mvp-server/src/main/java/com/example/llm/config/SecurityConfig.java): Spring Security 설정
- [`SwaggerConfig.java`](../mvp-server/src/main/java/com/example/llm/config/SwaggerConfig.java): OpenAPI/Swagger 설정
- [`GlobalExceptionHandler.java`](../mvp-server/src/main/java/com/example/llm/config/GlobalExceptionHandler.java): 전역 예외 처리
- [`ErrorResponse.java`](../mvp-server/src/main/java/com/example/llm/config/ErrorResponse.java): 에러 응답 DTO
- [`ValidationErrorResponse.java`](../mvp-server/src/main/java/com/example/llm/config/ValidationErrorResponse.java): 검증 에러 응답 DTO

#### [`controller/`](../mvp-server/src/main/java/com/example/llm/controller/)
- [`AuthController.java`](../mvp-server/src/main/java/com/example/llm/controller/AuthController.java): 인증 API (로그인, 로그아웃, 토큰 검증)
- [`TeamController.java`](../mvp-server/src/main/java/com/example/llm/controller/TeamController.java): 팀 관리 API
- [`ModelController.java`](../mvp-server/src/main/java/com/example/llm/controller/ModelController.java): 모델 관리 API
- [`DashboardController.java`](../mvp-server/src/main/java/com/example/llm/controller/DashboardController.java): 대시보드 API

#### [`dto/`](../mvp-server/src/main/java/com/example/llm/dto/)
- [`request/`](../mvp-server/src/main/java/com/example/llm/dto/request/): 요청 DTO
- [`response/`](../mvp-server/src/main/java/com/example/llm/dto/response/): 응답 DTO

#### [`entity/`](../mvp-server/src/main/java/com/example/llm/entity/)
- [`User.java`](../mvp-server/src/main/java/com/example/llm/entity/User.java): 사용자 엔티티
- [`Team.java`](../mvp-server/src/main/java/com/example/llm/entity/Team.java): 팀 엔티티
- [`TeamMember.java`](../mvp-server/src/main/java/com/example/llm/entity/TeamMember.java): 팀 멤버 엔티티
- [`Usage.java`](../mvp-server/src/main/java/com/example/llm/entity/Usage.java): 사용량 엔티티
- [`LLMModel.java`](../mvp-server/src/main/java/com/example/llm/entity/LLMModel.java): LLM 모델 엔티티

#### [`exception/`](../mvp-server/src/main/java/com/example/llm/exception/)
- [`BusinessException.java`](../mvp-server/src/main/java/com/example/llm/exception/BusinessException.java): 비즈니스 예외
- [`ResourceNotFoundException.java`](../mvp-server/src/main/java/com/example/llm/exception/ResourceNotFoundException.java): 리소스 찾기 실패 예외
- [`UnauthorizedException.java`](../mvp-server/src/main/java/com/example/llm/exception/UnauthorizedException.java): 인증 실패 예외
- [`ErrorCode.java`](../mvp-server/src/main/java/com/example/llm/exception/ErrorCode.java): 에러 코드 열거형

#### [`repository/`](../mvp-server/src/main/java/com/example/llm/repository/)
- [`UserRepository.java`](../mvp-server/src/main/java/com/example/llm/repository/UserRepository.java): 사용자 리포지토리
- [`TeamRepository.java`](../mvp-server/src/main/java/com/example/llm/repository/TeamRepository.java): 팀 리포지토리
- [`TeamMemberRepository.java`](../mvp-server/src/main/java/com/example/llm/repository/TeamMemberRepository.java): 팀 멤버 리포지토리
- [`UsageRepository.java`](../mvp-server/src/main/java/com/example/llm/repository/UsageRepository.java): 사용량 리포지토리
- [`ModelRepository.java`](../mvp-server/src/main/java/com/example/llm/repository/ModelRepository.java): 모델 리포지토리

#### [`service/`](../mvp-server/src/main/java/com/example/llm/service/)
- [`AuthService.java`](../mvp-server/src/main/java/com/example/llm/service/AuthService.java): 인증 서비스
- [`TeamService.java`](../mvp-server/src/main/java/com/example/llm/service/TeamService.java): 팀 서비스
- [`ModelService.java`](../mvp-server/src/main/java/com/example/llm/service/ModelService.java): 모델 서비스
- [`DashboardService.java`](../mvp-server/src/main/java/com/example/llm/service/DashboardService.java): 대시보드 서비스

#### [`util/`](../mvp-server/src/main/java/com/example/llm/util/)
- [`TokenUtil.java`](../mvp-server/src/main/java/com/example/llm/util/TokenUtil.java): JWT 토큰 유틸리티

---

## 개발 환경 설정

### 필수 요구사항

- **Java 17** 이상 설치 필요
- **Gradle 8.x** 이상 (Gradle Wrapper 포함)
- **Git** (버전 관리용)

### Java 17 설치 확인

```bash
java -version
# 출력: java version "17.x.x"
```

### 프로젝트 클론

```bash
git clone <repository-url>
cd demo/mvp-server
```

### IDE 설정 (IntelliJ IDEA)

1. IntelliJ IDEA에서 프로젝트 열기
2. File → Project Structure → Project → Project SDK: Java 17 선택
3. Build → Build Project 실행
4. Lombok 플러그인 활성화 확인

### VSCode 설정 (선택사항)

```json
{
  "java.home": "/path/to/java-17",
  "java.configuration.updateBuildConfiguration": "automatic"
}
```

---

## 빌드 및 실행 방법

### 빌드 명령어

```bash
# 전체 빌드
./gradlew build

# 테스트 제외 빌드
./gradlew build -x test

# JAR 파일 생성
./gradlew bootJar
```

### 실행 명령어

#### 개발 모드 실행 (권장)

```bash
cd mvp-server
./gradlew bootRun
```

#### JAR 파일 실행

```bash
java -jar build/libs/mvp-server.jar
```

#### 특정 프로파일로 실행

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### 테스트 실행

```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests AuthServiceTest

# 테스트 커버리지 리포트
./gradlew test jacocoTestReport
```

### 서버 접속 정보

서버가 성공적으로 시작되면 다음 URL로 접속할 수 있습니다:

| 서비스 | URL | 비고 |
|--------|-----|------|
| API Base URL | http://localhost:8080/api | REST API 기본 경로 |
| Swagger UI | http://localhost:8080/swagger-ui.html | API 문서 |
| H2 Console | http://localhost:8080/h2-console | DB 콘솔 |

---

## 엔티티 구조 및 관계

### ER 다이어그램

```
┌─────────────┐         ┌──────────────┐         ┌─────────────┐
│    User     │         │  TeamMember  │         │    Team     │
├─────────────┤         ├──────────────┤         ├─────────────┤
│ id (PK)     │<────────│ user_id (FK) │         │ id (PK)     │
│ username    │         │ team_id (FK) │────────>│ name        │
│ password    │         │ role         │         │ quota       │
│ email       │         │ created_at   │         │ usage       │
│ role        │         └──────────────┘         │ created_at  │
│ created_at  │                                    │ updated_at  │
└─────────────┘                                    └─────────────┘
                                                      ^
                                                      │
                                                      │
┌─────────────┐         ┌──────────────┐         ┌─────────────┐
│  LLMModel   │         │    Usage     │         │    Team     │
├─────────────┤         ├──────────────┤         └─────────────┘
│ id (PK)     │<────────│ model_id (FK) │
│ name        │         │ team_id (FK) │
│ provider    │         │ tokens       │
│ costPerToken│         │ cost         │
│ apiKey      │         │ used_at      │
│ isActive    │         └──────────────┘
│ created_at  │
│ updated_at  │
└─────────────┘
```

### 엔티티 상세 설명

#### 1. [`User`](../mvp-server/src/main/java/com/example/llm/entity/User.java)

사용자 정보를 저장하는 엔티티입니다.

| 필드 | 타입 | 설명 |
|------|------|------|
| id | Long | 기본 키 |
| username | String | 사용자 이름 (unique) |
| password | String | 비밀번호 (BCrypt 암호화) |
| email | String | 이메일 (unique) |
| role | String | 역할 (ADMIN, USER) |
| createdAt | LocalDateTime | 생성일시 |

**관계**: N:N with Team (through TeamMember)

#### 2. [`Team`](../mvp-server/src/main/java/com/example/llm/entity/Team.java)

팀 정보를 저장하는 엔티티입니다.

| 필드 | 타입 | 설명 |
|------|------|------|
| id | Long | 기본 키 |
| name | String | 팀 이름 |
| quota | Long | 할당량 (토큰 수) |
| usage | Long | 현재 사용량 |
| createdAt | LocalDateTime | 생성일시 |
| updatedAt | LocalDateTime | 수정일시 |

**관계**:
- 1:N with TeamMember
- 1:N with Usage

#### 3. [`TeamMember`](../mvp-server/src/main/java/com/example/llm/entity/TeamMember.java)

팀과 사용자 간의 관계를 나타내는 연결 엔티티입니다.

| 필드 | 타입 | 설명 |
|------|------|------|
| id | Long | 기본 키 |
| team | Team | 소속 팀 |
| user | User | 사용자 |
| role | String | 팀 내 역할 (ADMIN, MEMBER) |
| createdAt | LocalDateTime | 생성일시 |

**관계**:
- N:1 with Team
- N:1 with User

#### 4. [`Usage`](../mvp-server/src/main/java/com/example/llm/entity/Usage.java)

모델 사용 기록을 저장하는 엔티티입니다.

| 필드 | 타입 | 설명 |
|------|------|------|
| id | Long | 기본 키 |
| team | Team | 사용 팀 |
| model | LLMModel | 사용 모델 |
| tokens | Integer | 사용 토큰 수 |
| cost | Double | 비용 |
| usedAt | LocalDateTime | 사용 일시 |

**관계**:
- N:1 with Team
- N:1 with LLMModel

#### 5. [`LLMModel`](../mvp-server/src/main/java/com/example/llm/entity/LLMModel.java)

LLM 모델 정보를 저장하는 엔티티입니다.

| 필드 | 타입 | 설명 |
|------|------|------|
| id | Long | 기본 키 |
| name | String | 모델 이름 |
| provider | String | 제공사 (OpenAI, Anthropic 등) |
| costPerToken | Double | 토큰당 비용 |
| apiKey | String | API 키 |
| isActive | Boolean | 활성화 여부 |
| createdAt | LocalDateTime | 생성일시 |
| updatedAt | LocalDateTime | 수정일시 |

**관계**: 1:N with Usage

---

## 비즈니스 로직 설계

### 서비스 계층 아키텍처

```
Controller → Service → Repository → Entity
     ↓           ↓
  DTO 변환    비즈니스 로직
```

### 주요 서비스 설명

#### 1. [`AuthService`](../mvp-server/src/main/java/com/example/llm/service/AuthService.java)

**주요 기능**:
- 사용자 로그인 및 JWT 토큰 발급
- 로그아웃 처리
- 토큰 유효성 검증

**메서드**:
- [`login(LoginRequest)`](../mvp-server/src/main/java/com/example/llm/service/AuthService.java): 사용자 인증 및 토큰 생성
- [`logout(String token)`](../mvp-server/src/main/java/com/example/llm/service/AuthService.java): 토큰 무효화 (현재는 Mock 구현)
- [`validateToken(String token)`](../mvp-server/src/main/java/com/example/llm/service/AuthService.java): 토큰 유효성 확인

#### 2. [`TeamService`](../mvp-server/src/main/java/com/example/llm/service/TeamService.java)

**주요 기능**:
- 팀 CRUD 연산
- 팀 할당량 설정 및 관리
- 팀 멤버 관리

**메서드**:
- [`createTeam(TeamCreateRequest)`](../mvp-server/src/main/java/com/example/llm/service/TeamService.java): 팀 생성
- [`getAllTeams()`](../mvp-server/src/main/java/com/example/llm/service/TeamService.java): 전체 팀 목록 조회
- [`getTeamById(Long id)`](../mvp-server/src/main/java/com/example/llm/service/TeamService.java): 특정 팀 조회
- [`updateTeam(Long id, TeamUpdateRequest)`](../mvp-server/src/main/java/com/example/llm/service/TeamService.java): 팀 수정
- [`deleteTeam(Long id)`](../mvp-server/src/main/java/com/example/llm/service/TeamService.java): 팀 삭제
- [`setQuota(Long id, QuotaSetRequest)`](../mvp-server/src/main/java/com/example/llm/service/TeamService.java): 할당량 설정

#### 3. [`ModelService`](../mvp-server/src/main/java/com/example/llm/service/ModelService.java)

**주요 기능**:
- LLM 모델 CRUD 연산
- 모델 활성/비활성 상태 관리

**메서드**:
- [`createModel(ModelCreateRequest)`](../mvp-server/src/main/java/com/example/llm/service/ModelService.java): 모델 생성
- [`getAllModels()`](../mvp-server/src/main/java/com/example/llm/service/ModelService.java): 전체 모델 목록 조회
- [`getActiveModels()`](../mvp-server/src/main/java/com/example/llm/service/ModelService.java): 활성 모델 목록 조회
- [`getModelById(Long id)`](../mvp-server/src/main/java/com/example/llm/service/ModelService.java): 특정 모델 조회
- [`updateModel(Long id, ModelCreateRequest)`](../mvp-server/src/main/java/com/example/llm/service/ModelService.java): 모델 수정
- [`deleteModel(Long id)`](../mvp-server/src/main/java/com/example/llm/service/ModelService.java): 모델 삭제
- [`toggleModel(Long id)`](../mvp-server/src/main/java/com/example/llm/service/ModelService.java): 모델 활성 상태 토글

#### 4. [`DashboardService`](../mvp-server/src/main/java/com/example/llm/service/DashboardService.java)

**주요 기능**:
- 대시보드 통계 데이터 집계
- 팀별 사용량 요약
- 전체 통계 제공

**메서드**:
- [`getDashboard()`](../mvp-server/src/main/java/com/example/llm/service/DashboardService.java): 대시보드 데이터 조회

---

## API 엔드포인트 목록

### 인증 API

| 메서드 | 경로 | 설명 | 인증 |
|--------|------|------|------|
| POST | `/api/auth/login` | 로그인 | X |
| POST | `/api/auth/logout` | 로그아웃 | O |
| GET | `/api/auth/validate` | 토큰 검증 | O |

### 팀 관리 API

| 메서드 | 경로 | 설명 | 인증 |
|--------|------|------|------|
| GET | `/api/teams` | 팀 목록 조회 | O |
| POST | `/api/teams` | 팀 생성 | O |
| GET | `/api/teams/{id}` | 팀 상세 조회 | O |
| PUT | `/api/teams/{id}` | 팀 수정 | O |
| DELETE | `/api/teams/{id}` | 팀 삭제 | O |
| PATCH | `/api/teams/{id}/quota` | 할당량 설정 | O |

### 모델 관리 API

| 메서드 | 경로 | 설명 | 인증 |
|--------|------|------|------|
| GET | `/api/models` | 모델 목록 조회 | O |
| GET | `/api/models/active` | 활성 모델 조회 | O |
| POST | `/api/models` | 모델 생성 | O |
| GET | `/api/models/{id}` | 모델 상세 조회 | O |
| PUT | `/api/models/{id}` | 모델 수정 | O |
| DELETE | `/api/models/{id}` | 모델 삭제 | O |
| PATCH | `/api/models/{id}/toggle` | 모델 활성/비활성 | O |

### 대시보드 API

| 메서드 | 경로 | 설명 | 인증 |
|--------|------|------|------|
| GET | `/api/dashboard` | 대시보드 데이터 조회 | O |

---

## 테스트 방법

### 테스트 환경

- **프레임워크**: JUnit 5 (Spring Boot Test)
- **데이터베이스**: H2 인메모리 (테스트용)

### 테스트 실행

```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests AuthServiceTest

# 테스트 결과 보고서 확인
open build/reports/tests/test/index.html
```

### 테스트 작성 가이드

```java
@SpringBootTest
@AutoConfigureMockMvc
class ServiceTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    @DisplayName("테스트 설명")
    void testMethod() {
        // Given
        // 준비 단계
        
        // When
        // 실행 단계
        
        // Then
        // 검증 단계
    }
}
```

### 모킹 (Mocking)

```java
@ExtendWith(MockitoExtension.class)
class ServiceWithMockTest {
    
    @Mock
    private Repository repository;
    
    @InjectMocks
    private Service service;
}
```

---

## 코드 스타일 및 컨벤션

### Java 코드 컨벤션

1. **클래스 이름**: PascalCase (예: `UserService`)
2. **메서드 이름**: camelCase (예: `getUserById`)
3. **상수 이름**: UPPER_SNAKE_CASE (예: `MAX_RETRIES`)
4. **패키지 이름**: 소문자 (예: `com.example.llm`)

### Lombok 애너테이션 사용

```java
@Data           // Getter, Setter, toString, equals, hashCode
@Builder        // 빌더 패턴
@NoArgsConstructor  // 기본 생성자
@AllArgsConstructor // 전체 인자 생성자
@RequiredArgsConstructor // 필수 필드 생성자
```

### API 컨트롤러 컨벤션

```java
@RestController
@RequestMapping("/api/resource")
@RequiredArgsConstructor
@Tag(name = "리소스 관리", description = "리소스 CRUD API")
public class ResourceController {
    
    private final ResourceService resourceService;
    
    @PostMapping
    @Operation(summary = "생성", description = "설명")
    public ResponseEntity<ResponseDto> createResource(
            @Valid @RequestBody RequestDto request) {
        ResponseDto response = resourceService.create(request);
        return ResponseEntity.ok(response);
    }
}
```

### DTO 컨벤션

- Request DTO: `{Entity}CreateRequest`, `{Entity}UpdateRequest`
- Response DTO: `{Entity}Response`

---

## 개발 시 주의사항

### ⚠️ 현재 개발용 설정 (프로덕션 배포 전 변경 필요)

#### 1. 데이터베이스 설정

**현재**: H2 인메모리 데이터베이스
```properties
spring.datasource.url=jdbc:h2:mem:llmdb
```

**프로덕션 변경 필요**: MySQL, PostgreSQL 등 영구적 DB 사용
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/llmdb
```

#### 2. JWT Secret

**현재**: 고정된 개발용 키
```properties
jwt.secret=mvp-mock-secret-key-for-development-only
```

**프로덕션 변경 필요**: 환경 변수 또는 비밀 관리 시스템 사용
```bash
export JWT_SECRET=$(openssl rand -base64 64)
```

#### 3. 비밀번호

**현재**: [`data.sql`](../mvp-server/src/main/resources/data.sql)에 평문 해시 저장

**프로덕션 변경 필요**: 보안된 비밀번호 정책 적용

#### 4. 보안 설정

**현재**: [`SecurityConfig.java`](../mvp-server/src/main/java/com/example/llm/config/SecurityConfig.java)에서 모든 요청 허용
```java
.auth -> auth.anyRequest().permitAll()
```

**프로덕션 변경 필요**: 적절한 인증/인가 규칙 적용

### 추가 주의사항

1. **CORS 설정**: 현재 모든 출처 허용 - 프로덕션에서는 특정 도메인만 허용
2. **H2 Console**: 프로덕션에서는 반드시 비활성화 (`spring.h2.console.enabled=false`)
3. **SQL 로그**: 프로덕션에서는 SQL 로그 비활성화 (`spring.jpa.show-sql=false`)
4. **API Key**: 실제 API Key는 절대 코드에 저장하지 말고 환경 변수 사용
5. **예외 처리**: 민감한 정보는 에러 메시지에 포함하지 않기

### 개발 권장사항

1. **테스트 작성**: 새 기능 추가 시 반드시 테스트 코드 작성
2. **API 문서 업데이트**: Swagger 설명을 항상 최신 상태로 유지
3. **코드 리뷰**: Pull Request를 통한 코드 검토 수행
4. **커밋 메시지**: 명확한 커밋 메시지 작성 (Conventional Commits)

---

## 참고 자료

- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [Spring Data JPA 문서](https://spring.io/projects/spring-data-jpa)
- [Spring Security 문서](https://spring.io/projects/spring-security)
- [SpringDoc OpenAPI](https://springdoc.org/)
- [Lombok 문서](https://projectlombok.org/)

---

**문서 유지보수**: 이 문서는 프로젝트 변경 사항에 따라 지속적으로 업데이트되어야 합니다.
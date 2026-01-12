# LLM 관리 시스템 - 백엔드 서버

Spring Boot 3.2 + Java 17 + Gradle + H2 DB 기반의 LLM 모델 관리 시스템 백엔드 서버입니다.

## 사전 요구사항

- Java 17 이상
- Gradle 8.5 이상 (프로젝트 포함)

## 설치 및 실행 방법

### 1. Java 설치 확인

```bash
# Java 버전 확인 (17 이상 필요)
java --version
```

Java가 설치되어 있지 않다면:
- **macOS**: `brew install openjdk@17`
- **Linux**: `sudo apt install openjdk-17-jdk`
- **Windows**: [Oracle JDK 다운로드](https://www.oracle.com/java/technologies/downloads/)

### 2. Gradle 설치

#### macOS (Homebrew)

```bash
# Gradle 설치
brew install gradle

# 설치 확인
gradle --version
```

#### Linux (SDKMAN)

```bash
# SDKMAN 설치
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Gradle 설치
sdk install gradle 8.5

# 설치 확인
gradle --version
```

#### Windows

Gradle 공식 웹사이트에서 다운로드: https://gradle.org/install/
환경 변수 PATH에 Gradle bin 디렉토리를 추가하세요.

### 3. 프로젝트 실행

```bash
# 프로젝트 디렉토리로 이동
cd mvp-server

# 의존성 다운로드 및 빌드 (최초 1회 실행 시)
./gradlew build

# 개발 모드로 서버 실행
./gradlew bootRun
```

서버가 성공적으로 시작되면 다음 메시지가 표시됩니다:
```
Started MvpServerApplication in X.XXX seconds

## 빠른 시작 (Quick Start)

```bash
# 1. 프로젝트 클론 또는 디렉토리 이동
cd mvp-server

# 2. 의존성 다운로드 (최초 1회)
./gradlew build

# 3. 서버 실행
./gradlew bootRun
```

서버 시작 후:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:llmdb`)
- **API Base URL**: http://localhost:8080/api

## 테스트용 계정

서버가 자동으로 생성하는 테스트용 계정:
- **Username**: `admin` / **Password**: `password`
- **Username**: `user1` / **Password**: `password`

## 문제 해결

### Gradle 명령을 찾을 수 없을 때
```bash
# macOS
brew install gradle

# Linux
curl -s "https://get.sdkman.io" | bash
sdk install gradle 8.5
```

### Java 버전 오류
```bash
# Java 17 이상 필요
java --version

# macOS
brew install openjdk@17

# Linux
sudo apt install openjdk-17-jdk
```

### 포트 충돌
포트 8080이 이미 사용 중인 경우 `application.properties`에서 포트 변경:
```properties
server.port=8081
```

### Windows에서 gradlew.bat 실행 오류
```cmd
# PowerShell에서 실행할 경우
.\gradlew.bat bootRun

# 또는 Git Bash 사용
./gradlew bootRun
```

```

## 프로젝트 구조

```
mvp-server/
├── src/main/java/com/example/llm/
│   ├── config/           # 설정 (Security, Swagger, Exception Handler)
│   ├── controller/        # REST API 컨트롤러
│   ├── dto/              # Data Transfer Objects (Request/Response)
│   ├── entity/           # JPA 엔티티
│   ├── exception/         # 커스텀 예외
│   ├── repository/        # JPA 리포지토리
│   ├── service/          # 비즈니스 로직
│   └── util/            # 유틸리티 클래스
├── src/main/resources/
│   ├── application.properties  # 설정 파일
│   └── data.sql            # 초기 데이터
└── build.gradle           # Gradle 빌드 설정
```

## 실행 방법

### 개발 모드 실행

```bash
cd mvp-server
./gradlew bootRun
```

### JAR 빌드 및 실행

```bash
# JAR 빌드
./gradlew bootJar

# JAR 실행
java -jar build/libs/mvp-server.jar
```

## API 엔드포인트

### 인증 API
- `POST /api/auth/login` - 로그인
- `POST /api/auth/logout` - 로그아웃
- `GET /api/auth/validate` - 토큰 검증

### 팀 관리 API
- `GET /api/teams` - 팀 목록 조회
- `POST /api/teams` - 팀 생성
- `GET /api/teams/{id}` - 팀 상세 조회
- `PUT /api/teams/{id}` - 팀 수정
- `DELETE /api/teams/{id}` - 팀 삭제
- `PATCH /api/teams/{id}/quota` - 할당량 설정

### 모델 관리 API
- `GET /api/models` - 모델 목록 조회
- `POST /api/models` - 모델 생성
- `GET /api/models/active` - 활성 모델 조회
- `GET /api/models/{id}` - 모델 상세 조회
- `PUT /api/models/{id}` - 모델 수정
- `DELETE /api/models/{id}` - 모델 삭제
- `PATCH /api/models/{id}/toggle` - 모델 활성/비활성 토글

### 대시보드 API
- `GET /api/dashboard` - 대시보드 데이터 조회

## Swagger UI

서버 실행 후 다음 URL에서 API 문서 확인 가능:
- http://localhost:8080/swagger-ui.html

## H2 Database Console

개발용 H2 데이터베이스 콘솔:
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:llmdb`
- Username: `sa`
- Password: (비어 있음)

## 초기 데이터

서버 시작 시 다음 초기 데이터가 자동으로 생성됩니다:

- 사용자: admin, user1 (비밀번호: password)
- 팀: AI Research Team, Data Science Team, Product Team
- 모델: GPT-4, GPT-3.5-Turbo, Claude-2, Claude-Instant
- 사용량 데이터: 각 팀별 사용 이력

## 개발 참고사항

- JWT 비밀키는 개발용이며, 프로덕션 환경에서는 반드시 변경해야 합니다.
- H2 DB는 인메모리 DB이므로 서버 재시작 시 데이터가 초기화됩니다.
- 현재 모든 API 엔드포인트는 인증 없이 접근 가능합니다 (개발용 설정).

## 테스트

```bash
# 모든 테스트 실행
./gradlew test

# 특정 테스트 실행
./gradlew test --tests TeamServiceTest
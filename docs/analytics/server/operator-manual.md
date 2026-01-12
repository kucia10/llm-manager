# MVP Server 운영자 매뉴얼

## 문서 정보

- **버전**: 1.0.0
- **최종 수정일**: 2026-01-09
- **대상 독자**: 시스템 운영자, DevOps 엔지니어, 시스템 관리자

## 목차

1. [시스템 개요](#시스템-개요)
2. [시스템 요구사항](#시스템-요구사항)
3. [설치 방법](#설치-방법)
4. [서버 실행 방법](#서버-실행-방법)
5. [설정 파일 설명](#설정-파일-설명)
6. [환경 변수 설정](#환경-변수-설정)
7. [모니터링 방법](#모니터링-방법)
8. [로그 설정 및 확인](#로그-설정-및-확인)
9. [문제 해결 가이드](#문제-해결-가이드)
10. [백업 및 복구](#백업-및-복구)
11. [프로덕션 배포 시 고려사항](#프로덕션-배포-시-고려사항)
12. [보안 권장사항](#보안-권장사항)

---

## 시스템 개요

### 시스템 소개

MVP Server는 LLM(Large Language Model) 모델 관리 및 팀 할당량 관리를 위한 Spring Boot 기반 REST API 서버입니다. 이 시스템은 다음과 같은 핵심 기능을 제공합니다:

- 사용자 인증 및 권한 관리 (JWT 기반)
- LLM 모델 등록 및 활성화/비활성화 관리
- 팀 생성 및 멤버 관리
- 팀별 할당량(Quota) 설정 및 모니터링
- API 사용량 추적 및 비용 계산
- 대시보드 통계 데이터 제공

### 시스템 아키텍처

```
┌─────────────────────────────────────────────────────────┐
│                    Frontend (React)                      │
│                    mvc-client:5173                       │
└─────────────────────┬───────────────────────────────────┘
                      │ HTTP/HTTPS
                      │
┌─────────────────────┴───────────────────────────────────┐
│                  Backend (Spring Boot)                   │
│                    mvp-server:8080                        │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌──────────┐   │
│  │  Auth   │  │  Team   │  │  Model  │  │Dashboard │   │
│  │Service  │  │Service  │  │Service  │  │ Service  │   │
│  └────┬────┘  └────┬────┘  └────┬────┘  └────┬─────┘   │
│       └──────────────┴────────────┴─────────────┘      │
│                      │                                   │
│                ┌─────┴─────┐                            │
│                │    DB     │                            │
│                │  (H2/DB)  │                            │
│                └───────────┘                            │
└─────────────────────────────────────────────────────────┘
```

---

## 시스템 요구사항

### 하드웨어 요구사항

| 구성 항목 | 최소 사양 | 권장 사양 |
|-----------|-----------|-----------|
| CPU | 2 코어 | 4 코어 이상 |
| RAM | 2GB | 4GB 이상 |
| 디스크 | 10GB 여유 공간 | 20GB 이상 |
| 네트워크 | 100 Mbps | 1 Gbps |

### 소프트웨어 요구사항

| 소프트웨어 | 최소 버전 | 권장 버전 |
|-----------|-----------|-----------|
| Java | 17 (LTS) | 17+ |
| Operating System | Linux/macOS/Windows | Linux (Ubuntu 20.04+) |

### 포트 요구사항

| 포트 | 용도 | 외부 접근 |
|------|------|-----------|
| 8080 | 애플리케이션 (HTTP) | 필요 |
| 8081 | 애플리케이션 (HTTPS - 선택사항) | 필요 |
| 3306 | MySQL (선택사항) | 내부 전용 |
| 5432 | PostgreSQL (선택사항) | 내부 전용 |

---

## 설치 방법

### 1. Java 17 설치

#### Ubuntu/Debian

```bash
# OpenJDK 17 설치
sudo apt update
sudo apt install openjdk-17-jdk

# 설치 확인
java -version
```

#### macOS (Homebrew)

```bash
# Homebrew 설치 (없는 경우)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# OpenJDK 17 설치
brew install openjdk@17

# 설치 확인
java -version
```

#### Windows

1. [Adoptium](https://adoptium.net/)에서 JDK 17 다운로드
2. 설치 프로그램 실행
3. 환경 변수 JAVA_HOME 설정
4. PATH에 `%JAVA_HOME%\bin` 추가

### 2. 프로젝트 배포

#### 방법 1: Git 클론 (개발/테스트 환경)

```bash
# 프로젝트 클론
git clone <repository-url>
cd demo/mvp-server

# 의존성 다운로드
./gradlew build
```

#### 방법 2: JAR 파일 배포 (프로덕션 환경)

```bash
# JAR 파일 전송 (로컬에서 서버로)
scp build/libs/mvp-server.jar user@server:/opt/mvp-server/

# 서버에서 실행 디렉토리 생성
ssh user@server
sudo mkdir -p /opt/mvp-server
sudo chown -R $USER:$USER /opt/mvp-server

# JAR 파일 이동
mv mvp-server.jar /opt/mvp-server/
```

---

## 서버 실행 방법

### 개발 모드 실행

```bash
cd mvp-server
./gradlew bootRun
```

### 프로덕션 모드 실행 (JAR)

```bash
# 백그라운드 실행 (Linux/macOS)
nohup java -jar /opt/mvp-server/mvp-server.jar > /var/log/mvp-server.log 2>&1 &

# 또는 systemd 서비스로 실행 (권장)
# 자세한 내용은 "프로덕션 배포 시 고려사항" 참조
```

### 윈도우 서비스로 실행

```batch
# NSSM (Non-Sucking Service Manager) 사용
nssm install MVPServer "C:\Program Files\Java\jdk-17\bin\java.exe" "-jar" "C:\mvp-server\mvp-server.jar"
nssm start MVPServer
```

### 서버 상태 확인

```bash
# 프로세스 확인
ps aux | grep mvp-server

# 포트 확인
netstat -tlnp | grep 8080

# 헬스 체크 (API)
curl http://localhost:8080/actuator/health
```

### 서버 중지

```bash
# 프로세스 ID 찾기
ps aux | grep mvp-server

# 프로세스 종료
kill <PID>

# 강제 종료
kill -9 <PID>
```

---

## 설정 파일 설명

### application.properties

주요 설정 파일: [`src/main/resources/application.properties`](../mvp-server/src/main/resources/application.properties)

#### 서버 설정

```properties
# 서버 포트
server.port=8080

# 애플리케이션 이름
spring.application.name=mvp-server
```

#### 데이터베이스 설정 (H2 인메모리)

```properties
# H2 인메모리 DB (개발용)
spring.datasource.url=jdbc:h2:mem:llmdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# H2 콘솔 (개발용 - 프로덕션에서는 비활성화 필요)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

#### 데이터베이스 설정 (MySQL - 프로덕션 권장)

```properties
# MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/llmdb?useSSL=false&serverTimezone=Asia/Seoul
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

#### 데이터베이스 설정 (PostgreSQL - 프로덕션 권장)

```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/llmdb
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

#### JPA 설정

```properties
# JPA 다이얼렉트
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# DDL 자동 생성 (개발: update, 프로덕션: validate/none)
spring.jpa.hibernate.ddl-auto=update

# SQL 로그 출력 (개발: true, 프로덕션: false)
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# 데이터 초기화
spring.sql.init.mode=always
spring.jpa.defer-datasource-initialization=true
```

#### JWT 설정

```properties
# JWT 비밀키 (프로덕션에서는 환경 변수 사용 권장)
jwt.secret=mvp-mock-secret-key-for-development-only

# JWT 만료 시간 (밀리초) - 24시간
jwt.expiration=86400000
```

#### 로깅 설정

```properties
# 애플리케이션 로그 레벨
logging.level.com.example.llm=DEBUG
logging.level.org.springframework.web=INFO
logging.level.org.hibernate.SQL=DEBUG
```

### application-{profile}.properties

프로필별 설정 파일을 생성하여 환경별 구성을 관리할 수 있습니다.

#### application-dev.properties (개발 환경)

```properties
# H2 DB 사용
spring.datasource.url=jdbc:h2:mem:llmdb
spring.h2.console.enabled=true
logging.level.com.example.llm=DEBUG
```

#### application-prod.properties (프로덕션 환경)

```properties
# MySQL DB 사용
spring.datasource.url=jdbc:mysql://prod-db:3306/llmdb
spring.h2.console.enabled=false
logging.level.com.example.llm=INFO
```

---

## 환경 변수 설정

### 환경 변수 목록

| 변수명 | 설명 | 필수 여부 | 기본값 |
|--------|------|-----------|--------|
| `DB_USERNAME` | 데이터베이스 사용자명 | 아니요 | sa |
| `DB_PASSWORD` | 데이터베이스 비밀번호 | 아니요 | (빈값) |
| `DB_URL` | 데이터베이스 연결 URL | 아니요 | jdbc:h2:mem:llmdb |
| `JWT_SECRET` | JWT 서명 비밀키 | 아니요 | (개발용 키) |
| `JWT_EXPIRATION` | JWT 만료 시간 (ms) | 아니요 | 86400000 |
| `SERVER_PORT` | 서버 포트 | 아니요 | 8080 |

### 환경 변수 설정 방법

#### Linux/macOS (Shell 스크립트)

```bash
# .env 파일 생성
cat > /opt/mvp-server/.env << EOF
export DB_USERNAME=admin
export DB_PASSWORD=secure_password
export DB_URL=jdbc:mysql://localhost:3306/llmdb
export JWT_SECRET=$(openssl rand -base64 64)
export SERVER_PORT=8080
EOF

# 환경 변수 로드
source /opt/mvp-server/.env

# 서버 실행
java -jar /opt/mvp-server/mvp-server.jar
```

#### systemd 서비스 (Linux)

```ini
# /etc/systemd/system/mvp-server.service
[Unit]
Description=MVP Server
After=network.target

[Service]
Type=simple
User=mvp-server
Group=mvp-server
WorkingDirectory=/opt/mvp-server
Environment="DB_USERNAME=admin"
Environment="DB_PASSWORD=secure_password"
Environment="DB_URL=jdbc:mysql://localhost:3306/llmdb"
Environment="JWT_SECRET=your-secret-key"
ExecStart=/usr/bin/java -jar /opt/mvp-server/mvp-server.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

```bash
# 서비스 등록
sudo systemctl enable mvp-server

# 서비스 시작
sudo systemctl start mvp-server

# 서비스 상태 확인
sudo systemctl status mvp-server
```

#### Docker (Docker Compose)

```yaml
# docker-compose.yml
version: '3.8'

services:
  mvp-server:
    image: openjdk:17-jdk-slim
    container_name: mvp-server
    working_dir: /app
    volumes:
      - ./mvp-server.jar:/app/mvp-server.jar
    ports:
      - "8080:8080"
    environment:
      - DB_URL=jdbc:mysql://mysql:3306/llmdb
      - DB_USERNAME=root
      - DB_PASSWORD=rootpassword
      - JWT_SECRET=${JWT_SECRET}
    depends_on:
      - mysql
    command: java -jar mvp-server.jar
    restart: unless-stopped

  mysql:
    image: mysql:8.0
    container_name: mvp-mysql
    environment:
      - MYSQL_ROOT_PASSWORD=rootpassword
      - MYSQL_DATABASE=llmdb
    volumes:
      - mysql-data:/var/lib/mysql
    ports:
      - "3306:3306"
    restart: unless-stopped

volumes:
  mysql-data:
```

---

## 모니터링 방법

### 1. Swagger UI (API 문서)

**URL**: http://localhost:8080/swagger-ui.html

Swagger UI를 통해 다음을 확인할 수 있습니다:
- 모든 API 엔드포인트
- 요청/응답 형식
- 실시간 API 테스트
- 인증 토큰 설정

**사용법**:
1. 브라우저에서 Swagger UI 접속
2. 우측 상단의 "Authorize" 버튼 클릭
3. 로그인 API로 획득한 JWT 토큰 입력 (`Bearer {token}` 형식)
4. 각 API를 직접 테스트

### 2. H2 Console (데이터베이스 콘솔)

**URL**: http://localhost:8080/h2-console

**개발 환경에서만 사용 가능합니다. 프로덕션에서는 반드시 비활성화해야 합니다.**

**접속 정보**:
- JDBC URL: `jdbc:h2:mem:llmdb`
- User Name: `sa`
- Password: (비어있음)

**사용법**:
1. 브라우저에서 H2 Console 접속
2. 위 접속 정보 입력 후 "Connect" 클릭
3. SQL 쿼리 실행 가능

**주요 쿼리 예시**:

```sql
-- 모든 팀 조회
SELECT * FROM teams;

-- 모든 사용자 조회
SELECT * FROM users;

-- 팀별 사용량 집계
SELECT t.id, t.name, t.quota, t.usage, 
       (t.usage * 100.0 / t.quota) as usage_percent
FROM teams t;

-- 최근 사용 기록
SELECT * FROM usage 
ORDER BY used_at DESC 
LIMIT 10;

-- 활성 모델 목록
SELECT * FROM llm_models WHERE is_active = true;
```

### 3. 애플리케이션 로그 모니터링

```bash
# 실시간 로그 확인
tail -f /var/log/mvp-server.log

# 에러 로그만 확인
tail -f /var/log/mvp-server.log | grep ERROR

# 최근 100줄 확인
tail -n 100 /var/log/mvp-server.log
```

### 4. 시스템 리소스 모니터링

```bash
# CPU/메모리 사용률
htop

# 디스크 사용량
df -h

# 프로세스 상태
ps aux | grep java

# 네트워크 연결
netstat -tlnp | grep 8080
```

### 5. 헬스 체크 (선택사항)

Spring Boot Actuator를 추가하면 헬스 체크 기능을 사용할 수 있습니다.

```bash
# 헬스 상태 확인
curl http://localhost:8080/actuator/health

# 응답 예시
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "database": "H2",
      "validationQuery": "isValid()"
    },
    "diskSpace": {
      "status": "UP",
      "total": 499963174912,
      "free": 105910005760,
      "threshold": 10485760,
      "path": "./"
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

---

## 로그 설정 및 확인

### 로그 파일 위치

기본 로그 파일 위치:
- 개발 환경: 콘솔 출력
- 프로덕션 환경: `/var/log/mvp-server.log`

### 로그 레벨

| 레벨 | 설명 |
|------|------|
| ERROR | 오류 및 예외 상황 |
| WARN | 경고 (잠재적 문제) |
| INFO | 일반적인 정보 (시작/종료, 주요 이벤트) |
| DEBUG | 디버깅용 상세 정보 |
| TRACE | 가장 상세한 추적 정보 |

### 로그 레벨 변경

#### application.properties 설정

```properties
# 전체 로그 레벨
logging.level.root=INFO

# 패키지별 로그 레벨
logging.level.com.example.llm=DEBUG
logging.level.org.springframework.web=INFO
logging.level.org.hibernate.SQL=DEBUG

# 파일 로깅
logging.file.name=/var/log/mvp-server.log
logging.file.max-size=10MB
logging.file.max-history=30
```

#### 로그백 (Logback) 설정 (logback-spring.xml)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <springProfile name="dev">
        <root level="DEBUG">
            <appender-ref ref="CONSOLE"/>
        </root>
    </springProfile>
    
    <springProfile name="prod">
        <root level="INFO">
            <appender-ref ref="FILE"/>
            <appender-ref ref="ERROR_FILE"/>
        </root>
    </springProfile>
    
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>/var/log/mvp-server.log</file>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>/var/log/mvp-server.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
    </appender>
</configuration>
```

### 로그 분석 도구

```bash
# 에러 발생 횟수 통계
grep ERROR /var/log/mvp-server.log | wc -l

# 특정 기간 로그 확인
grep "2026-01-09 12:" /var/log/mvp-server.log

# 느린 요청 찾기 (100ms 이상)
grep "took.*ms" /var/log/mvp-server.log | awk '$4 > 100'

# 로그 파일 압축 및 보관
find /var/log -name "mvp-server*.log" -mtime +7 -exec gzip {} \;
```

---

## 문제 해결 가이드

### 1. 서버가 시작되지 않는 경우

#### 증상
- `./gradlew bootRun` 또는 `java -jar` 실행 시 에러 발생

#### 원인 및 해결책

**원인 1: 포트가 이미 사용 중**
```bash
# 포트 사용 중인 프로세스 확인
lsof -i :8080

# 다른 포트로 실행
./gradlew bootRun --args='--server.port=8081'
```

**원인 2: Java 버전 불일치**
```bash
# Java 버전 확인
java -version

# Java 17 설치 필요
sudo apt install openjdk-17-jdk
```

**원인 3: 데이터베이스 연결 실패**
```bash
# 데이터베이스 상태 확인
# MySQL
systemctl status mysql

# PostgreSQL
systemctl status postgresql

# 연결 테스트
mysql -u root -p -h localhost
```

### 2. API 요청 실패

#### 증상
- 404 Not Found, 500 Internal Server Error 등

#### 원인 및 해결책

**404 Not Found**
- URL 경로 확인 (`/api/...` 접두사 확인)
- Swagger UI에서 올바른 경로 확인

**401 Unauthorized**
- JWT 토큰 확인
- 토큰 만료 여부 확인
- Authorization 헤더 형식 확인 (`Bearer {token}`)

**500 Internal Server Error**
- 로그 확인 (`tail -f /var/log/mvp-server.log`)
- 데이터베이스 연결 확인
- 예외 스택 트레이스 분석

### 3. 데이터베이스 관련 문제

#### H2 인메모리 DB 문제

**문제**: 데이터가 서버 재시작 시 초기화됨
- 정상 동작입니다. H2 인메모리 DB는 재시작 시 데이터가 초기화됩니다.
- 영구적 데이터 저장이 필요하면 MySQL/PostgreSQL 사용을 권장합니다.

**해결책**:
```properties
# H2 파일 기반 DB로 변경
spring.datasource.url=jdbc:h2:file:/opt/mvp-server/data/llmdb;DB_CLOSE_ON_EXIT=FALSE
```

#### MySQL 연결 실패

```bash
# MySQL 접속 허용 설정
sudo nano /etc/mysql/mysql.conf.d/mysqld.cnf
# bind-address = 0.0.0.0 으로 변경

# MySQL 재시작
sudo systemctl restart mysql

# 사용자 권한 확인
mysql -u root -p
GRANT ALL PRIVILEGES ON llmdb.* TO 'admin'@'localhost';
FLUSH PRIVILEGES;
```

### 4. 메모리 부족 문제

#### 증상
- `OutOfMemoryError: Java heap space`

#### 해결책

```bash
# 힙 메모리 크기 증가
java -Xmx2G -Xms1G -jar mvp-server.jar

# 또는 application.properties 설정
spring.jpa.properties.hibernate.jdbc.batch_size=20
```

### 5. 로그인 실패

#### 증상
- 로그인 API에서 인증 실패

#### 해결책

```bash
# 초기 사용자 확인 (data.sql)
SELECT * FROM users;

# 비밀번호 재설정
# 개발 환경: data.sql 수정 후 서버 재시작
# 프로덕션: 비밀번호 변경 API 또는 DB 직접 수정

# 비밀번호 해시 확인
# BCrypt로 해싱된 비밀번호 사용
```

### 6. CORS 오류

#### 증상
- 브라우저에서 "CORS policy" 오류 발생

#### 해결책

SecurityConfig에서 CORS 설정 확인:
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

---

## 백업 및 복구

### 현재 설정 (H2 인메모리)

**중요**: 현재 H2 인메모리 DB를 사용 중이므로, **서버 재시작 시 모든 데이터가 초기화됩니다.**

### H2 파일 기반 DB 사용 (데이터 지속)

#### 1. application.properties 설정 변경

```properties
# 인메모리 → 파일 기반 DB로 변경
spring.datasource.url=jdbc:h2:file:/opt/mvp-server/data/llmdb;DB_CLOSE_ON_EXIT=FALSE;AUTO_RECONNECT=TRUE
spring.datasource.username=sa
spring.datasource.password=

# 데이터 디렉토리 생성
mkdir -p /opt/mvp-server/data
```

#### 2. 데이터 백업

```bash
# H2 데이터 파일 백업
cp /opt/mvp-server/data/llmdb.mv.db /backup/mvp-server-$(date +%Y%m%d).db

# 백업 스크립트 예시
#!/bin/bash
BACKUP_DIR="/backup"
DATA_FILE="/opt/mvp-server/data/llmdb.mv.db"
DATE=$(date +%Y%m%d_%H%M%S)

mkdir -p $BACKUP_DIR
cp $DATA_FILE $BACKUP_DIR/llmdb_$DATE.db
find $BACKUP_DIR -name "llmdb_*.db" -mtime +7 -delete
```

#### 3. 데이터 복구

```bash
# 서버 중지
sudo systemctl stop mvp-server

# 백업 파일 복사
cp /backup/mvp-server-20260109.db /opt/mvp-server/data/llmdb.mv.db

# 서버 시작
sudo systemctl start mvp-server
```

### MySQL/PostgreSQL 사용 시 백업

#### MySQL 백업

```bash
# 전체 데이터베이스 백업
mysqldump -u root -p llmdb > /backup/llmdb_$(date +%Y%m%d).sql

# 백업 복구
mysql -u root -p llmdb < /backup/llmdb_20260109.sql

# 자동 백업 스크립트 (crontab)
0 2 * * * /usr/bin/mysqldump -u root -ppassword llmdb > /backup/llmdb_$(date +\%Y\%m\%d).sql
```

#### PostgreSQL 백업

```bash
# 전체 데이터베이스 백업
pg_dump -U postgres llmdb > /backup/llmdb_$(date +%Y%m%d).sql

# 백업 복구
psql -U postgres llmdb < /backup/llmdb_20260109.sql
```

### 백업 정책 권장사항

| 백업 유형 | 주기 | 보관 기간 |
|-----------|------|-----------|
| 전체 백업 | 매일 (새벽 2시) | 30일 |
| 증분 백업 | 매시간 | 7일 |
| 로그 백업 | 실시간 | 90일 |

---

## 프로덕션 배포 시 고려사항

### 1. 데이터베이스 변경

**현재**: H2 인메모리
**권장**: MySQL 8.0+ 또는 PostgreSQL 14+

#### MySQL 마이그레이션

```properties
# application-prod.properties
spring.datasource.url=jdbc:mysql://prod-db:3306/llmdb?useSSL=true&serverTimezone=Asia/Seoul
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

**의존성 추가 (build.gradle)**:
```gradle
runtimeOnly 'mysql:mysql-connector-java:8.0.33'
```

### 2. HTTPS 적용

#### SSL/TLS 인증서 설정

```properties
server.port=8443
server.ssl.enabled=true
server.ssl.key-store=/opt/ssl/keystore.p12
server.ssl.key-store-password=${KEYSTORE_PASSWORD}
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=mvp-server
```

#### HTTP에서 HTTPS로 리다이렉트

```java
@Configuration
public class HttpsRedirectConfig {
    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
        tomcat.addAdditionalTomcatConnectors(redirectConnector());
        return tomcat;
    }
    
    private Connector redirectConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(false);
        connector.setRedirectPort(8443);
        return connector;
    }
}
```

### 3. 보안 강화

#### JWT 비밀키 관리

```bash
# 안전한 JWT 비밀키 생성
export JWT_SECRET=$(openssl rand -base64 64)

# 환경 변수로 설정
echo "export JWT_SECRET=$JWT_SECRET" >> ~/.bashrc
```

#### 비밀번호 정책

- 최소 12자 이상
- 대소문자, 숫자, 특수문자 포함
- 정기적인 비밀번호 변경 권장

#### API Key 보안

- 절대 코드에 하드코딩하지 않기
- 환경 변수 또는 비밀 관리 시스템(AWS Secrets Manager, HashiCorp Vault) 사용
- Git 저장소에 커밋하지 않기 (.gitignore 추가)

### 4. 로깅 및 모니터링

#### ELK Stack (Elasticsearch, Logstash, Kibana)

```properties
# Logstash 전송 설정
logging.logstash.host=logstash-server:5044
```

#### Prometheus + Grafana

```gradle
// 의존성 추가
implementation 'io.micrometer:micrometer-registry-prometheus'
implementation 'org.springframework.boot:spring-boot-starter-actuator'
```

```properties
# Actuator 엔드포인트 활성화
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.metrics.export.prometheus.enabled=true
```

### 5. 로드 밸런싱

#### Nginx 리버스 프록시 설정

```nginx
upstream mvp_server {
    server 10.0.0.1:8080;
    server 10.0.0.2:8080;
    server 10.0.0.3:8080;
}

server {
    listen 80;
    server_name api.example.com;
    
    location / {
        proxy_pass http://mvp_server;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### 6. systemd 서비스 설정

```ini
# /etc/systemd/system/mvp-server.service
[Unit]
Description=MVP Server
After=network.target mysql.service

[Service]
Type=simple
User=mvp-server
Group=mvp-server
WorkingDirectory=/opt/mvp-server
EnvironmentFile=/opt/mvp-server/.env
ExecStart=/usr/bin/java -Xms2g -Xmx4g -jar /opt/mvp-server/mvp-server.jar
ExecStop=/bin/kill -15 $MAINPID
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=mvp-server

[Install]
WantedBy=multi-user.target
```

```bash
# 서비스 활성화 및 시작
sudo systemctl daemon-reload
sudo systemctl enable mvp-server
sudo systemctl start mvp-server

# 로그 확인
sudo journalctl -u mvp-server -f
```

### 7. Docker 컨테이너화

```dockerfile
# Dockerfile
FROM openjdk:17-jdk-slim

LABEL maintainer="your-email@example.com"

WORKDIR /app

# JAR 파일 복사
COPY build/libs/mvp-server.jar app.jar

# 사용자 생성
RUN groupadd -r mvp-server && useradd -r -g mvp-server mvp-server
RUN chown -R mvp-server:mvp-server /app

USER mvp-server

# 포트 노출
EXPOSE 8080

# JVM 옵션
ENV JAVA_OPTS="-Xms2g -Xmx4g -XX:+UseG1GC"

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

```bash
# Docker 이미지 빌드
docker build -t mvp-server:1.0.0 .

# 컨테이너 실행
docker run -d \
  --name mvp-server \
  -p 8080:8080 \
  -e DB_URL=jdbc:mysql://mysql:3306/llmdb \
  -e DB_USERNAME=admin \
  -e DB_PASSWORD=password \
  mvp-server:1.0.0
```

---

## 보안 권장사항

### 1. 기본 보안 설정

#### 프로덕션 환경 필수 변경사항

| 설정 항목 | 개발 환경 | 프로덕션 환경 |
|-----------|-----------|---------------|
| H2 Console | 활성화 | **비활성화** |
| JWT Secret | 고정값 | **환경 변수 사용** |
| DB Password | 빈값/고정값 | **강력한 비밀번호** |
| SQL 로그 | 출력 | **비활성화** |
| CORS | 모든 출처 허용 | **특정 도메인만 허용** |

```properties
# 프로덕션 설정 (application-prod.properties)
spring.h2.console.enabled=false
spring.jpa.show-sql=false
logging.level.com.example.llm=INFO
```

### 2. API 보안

#### 인증/인가

- 모든 엔드포인트에 JWT 인증 적용 (개발용 제외)
- 역할 기반 접근 제어(RBAC) 구현
- 토큰 만료 시간 적절히 설정 (현재: 24시간)
- 리프레시 토큰 구현 고려

```java
// SecurityConfig - 프로덕션 권장 설정
.auth -> auth
    .requestMatchers("/api/auth/login").permitAll()
    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
    .anyRequest().authenticated()
```

#### Rate Limiting

```gradle
// 의존성 추가
implementation 'org.springframework.boot:spring-boot-starter-cache'
implementation 'com.github.vladimir-bukhtoyarov:bucket4j-core:7.6.0'
```

### 3. 데이터 보안

#### 데이터베이스 보안

- 최소 권한 원칙 적용
- 데이터베이스 사용자 분리 (읽기/쓰기 권한 분리)
- 정기적인 보안 패치 적용
- 데이터베이스 암호화 (휴지 데이터/전송 데이터)

#### 민감 정보 암호화

```bash
# 비밀번호 암호화 (BCrypt 사용)
# 이미 구현됨 - PasswordEncoder bean 사용

# API Key 암호화
# 환경 변수 또는 비밀 관리 시스템 사용
```

### 4. 네트워크 보안

#### 방화벽 설정

```bash
# UFW (Ubuntu)
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow from 10.0.0.0/8 to any port 3306
sudo ufw enable
```

#### SSL/TLS 적용

- HTTPS 필수 사용
- 강력한 암호화 알고리즘 사용
- 정기적인 인증서 갱신

### 5. 로그 및 감사

- 보안 이벤트 로깅
- 접근 로그 보관
- 정기적인 보안 감사 수행

### 6. 취약점 관리

- 정기적인 의존성 업데이트
- 취약점 스캔 실행 (OWASP Dependency-Check)
- 보안 패치 적용

```bash
# 의존성 취약점 스캔
./gradlew dependencyCheckAnalyze
```

### 7. 비상 대응 계획

- 보안 사고 대응 절차 수립
- 연락처 및 에스컬레이션 경로 정의
- 정기적인 보안 훈련 및 시뮬레이션

---

## 부록

### A. 유용한 명령어

```bash
# Java 버전 확인
java -version

# Gradle 버전 확인
./gradlew --version

# 포트 사용 중인 프로세스 찾기
lsof -i :8080

# 프로세스 종료
kill <PID>

# 로그 실시간 확인
tail -f /var/log/mvp-server.log

# 로그에서 에러 찾기
grep ERROR /var/log/mvp-server.log

# 서비스 상태 확인
systemctl status mvp-server

# 디스크 사용량 확인
df -h

# 메모리 사용량 확인
free -h

# CPU 사용량 확인
top
```

### B. 연락처

- **개발팀**: dev@example.com
- **운영팀**: ops@example.com
- **긴급 연락처**: +82-10-XXXX-XXXX

### C. 참고 문서

- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [Spring Security 참고 가이드](https://spring.io/projects/spring-security)
- [OWASP 보안 가이드](https://owasp.org/)
- [운영자 매뉴얼 개발자 버전](./developer-manual.md)

---

**문서 유지보수**: 이 문서는 시스템 변경 및 보안 요구사항에 따라 주기적으로 검토 및 업데이트되어야 합니다.
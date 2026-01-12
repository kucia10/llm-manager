# 백엔드(B/E) MVP WBS (Work Breakdown Structure)

## 역할 개요

- **담당자:** 백엔드 개발자 (1명)
- **기간:** 5일 (0.5 M/M)
- **총 M/M:** 0.5 M/M
- **주요 책임:** Spring Boot 기반 Mock API 개발, In-Memory DB 구성, API 명세서 작성

---

## 백엔드 MVP 제약 조건

1. **실제 DB 연동 없음:** PostgreSQL/MySQL 대신 H2 In-Memory DB 사용
2. **마이크로서비스 아키텍처 제외:** 단일 Monolithic 구조
3. **실제 LLM API 연동 제외:** Mock 데이터 활용
4. **보안 구현 제외:** HTTPS, 암호화, MFA 제외
5. **CI/CD 제외:** 로컬 개발만 수행
6. **테스트 커버리지:** 기본 수준 (단순 동작 확인)

---

## 백엔드 담당 작업 일정 (MVP 기간)

| Phase | 기간 | 주요 작업 | 소요 M/M |
|-------|------|----------|----------|
| Phase 1 | Day 3 | 프로젝트 초기화 및 Mock DB 구축 | 0.1 |
| Phase 2 | Day 4-5 | 핵심 API 개발 | 0.25 |
| Phase 3 | Day 6-7 | 추가 API 및 API 명세서 | 0.15 |
| **합계** | **5일** | | **0.5** |

---

## 상세 WBS (Task 단위)

### WBS-BE-MVP-01: 프로젝트 초기화 및 Mock DB 구축
**기간:** Day 3
**담당:** B/E 개발자
**소요 M/M:** 0.1

| Task ID | 작업 내용 | 상세 내용 | 산출물 |
|---------|----------|----------|--------|
| BE-MVP-01-01 | Spring Boot 프로젝트 초기화 | Spring Boot 프로젝트 생성, 기본 설정 | 프로젝트 구조 |
| BE-MVP-01-02 | H2 In-Memory DB 설정 | application.properties에 H2 설정 | DB 설정 파일 |
| BE-MVP-01-03 | Mock 데이터 초기화 | data.sql로 초기 Mock 데이터 삽입 | Mock 데이터 SQL |
| BE-MVP-01-04 | 기본 프로젝트 구조 설정 | controller, service, entity, repository 패키지 구성 | 폴더 구조 |

---

### WBS-BE-MVP-02: 인증 API 개발 (Mock)
**기간:** Day 3-4
**담당:** B/E 개발자
**관련 US:** US-01
**소요 M/M:** 0.1

| Task ID | 작업 내용 | 상세 내용 | 산출물 |
|---------|----------|----------|--------|
| BE-MVP-02-01 | User 엔티티 설계 및 구현 | User 엔티티, Repository 구현 | User 엔티티 |
| BE-MVP-02-02 | 로그인 API 구현 (Mock) | 하드코딩된 로그인 로직, 토큰 발급 Mock | 로그인 API |
| BE-MVP-02-03 | 토큰 검증 인터셉터 구현 | 간단한 토큰 검증 로직 (Mock) | 인증 인터셉터 |

**참고:**
- 실제 OAuth2.0 구현 없이 하드코딩된 로그인
- 토큰은 UUID 또는 간단한 문자열로 발급
- MFA 제외

---

### WBS-BE-MVP-03: 팀 관리 API 개발
**기간:** Day 4-5
**담당:** B/E 개발자
**관련 US:** US-04, US-05
**소요 M/M:** 0.1

| Task ID | 작업 내용 | 상세 내용 | 산출물 |
|---------|----------|----------|--------|
| BE-MVP-03-01 | Team 엔티티 설계 및 구현 | Team 엔티티, Repository 구현 | Team 엔티티 |
| BE-MVP-03-02 | TeamMember 엔티티 설계 및 구현 | TeamMember 엔티티, Repository 구현 | TeamMember 엔티티 |
| BE-MVP-03-03 | 팀 생성 API 구현 | 팀 생성 로직, H2 DB 저장 | 팀 생성 API |
| BE-MVP-03-04 | 팀 조회 API 구현 | 팀 목록, 상세 조회 API | 팀 조회 API |
| BE-MVP-03-05 | 팀 수정/삭제 API 구현 | 팀 수정, Soft Delete API | 팀 수정/삭제 API |
| BE-MVP-03-06 | 팀원 관리 API 구현 | 팀원 추가, 삭제, 역할 지정 API | 팀원 관리 API |

**참고:**
- 할당량 관리는 단순 필드로만 저장
- 복잡한 비즈니스 로직 제외

---

### WBS-BE-MVP-04: LLM 모델 관리 API 개발 (Mock)
**기간:** Day 5-6
**담당:** B/E 개발자
**관련 US:** US-06, US-07
**소요 M/M:** 0.08

| Task ID | 작업 내용 | 상세 내용 | 산출물 |
|---------|----------|----------|--------|
| BE-MVP-04-01 | LLMModel 엔티티 설계 및 구현 | LLMModel 엔티티, Repository 구현 | LLMModel 엔티티 |
| BE-MVP-04-02 | 모델 등록 API 구현 | 모델 API, 비용 정보 등록 API (Mock) | 모델 등록 API |
| BE-MVP-04-03 | 모델 조회 API 구현 | 모델 목록, 상세 조회 API | 모델 조회 API |
| BE-MVP-04-04 | 모델 수정/삭제 API 구현 | 모델 수정, Soft Delete API | 모델 수정/삭제 API |
| BE-MVP-04-05 | 모델 활성/비활성 API 구현 | 모델 상태 토글 API | 모델 상태 API |

**참고:**
- 실제 모델 API 연동 없음
- API 키는 평문 또는 단순 암호화
- 활성/비활성은 상태 필드만 변경

---

### WBS-BE-MVP-05: 사용량 및 대시보드 API 개발 (Mock)
**기간:** Day 6-7
**담당:** B/E 개발자
**관련 US:** US-08, US-09, US-10, US-13
**소요 M/M:** 0.07

| Task ID | 작업 내용 | 상세 내용 | 산출물 |
|---------|----------|----------|--------|
| BE-MVP-05-01 | Usage 엔티티 설계 및 구현 | Usage 엔티티, Repository 구현 | Usage 엔티티 |
| BE-MVP-05-02 | 팀 할당량 설정 API 구현 | 팀별 할당량 설정 API (Mock) | 할당량 설정 API |
| BE-MVP-05-03 | 개인 할당량 설정 API 구현 | 개인별 할당량 설정 API (Mock) | 할당량 설정 API |
| BE-MVP-05-04 | 대시보드 데이터 API 구현 | 총 사용량, 활성 사용자 Mock 데이터 반환 | 대시보드 API |
| BE-MVP-05-05 | 통계 데이터 API 구현 | 기본 통계 Mock 데이터 반환 | 통계 API |

**참고:**
- 실시간 사용량 모니터링 없음 (Mock 데이터만 반환)
- WebSocket 제외
- 복잡한 통계 집계 로직 제외

---

### WBS-BE-MVP-06: API 명세서 작성
**기간:** Day 7
**담당:** B/E 개발자
**소요 M/M:** 0.05

| Task ID | 작업 내용 | 상세 내용 | 산출물 |
|---------|----------|----------|--------|
| BE-MVP-06-01 | Swagger 설정 | SpringDoc OpenAPI 설정 | Swagger UI |
| BE-MVP-06-02 | API 명세 작성 | 각 API 명세, 예시 작성 | API 명세서 |
| BE-MVP-06-03 | API 테스트 가능하도록 제공 | Swagger UI로 API 테스트 가능 | Swagger UI |

---

## 백엔드 MVP 산출물 목록

### 1. 프로젝트 구조 및 설정
- Spring Boot 프로젝트 구조
- H2 In-Memory DB 설정
- Mock 데이터 (data.sql)
- application.properties

### 2. 엔티티 및 Repository
- User 엔티티
- Team 엔티티
- TeamMember 엔티티
- LLMModel 엔티티
- Usage 엔티티

### 3. API 엔드포인트
- 인증 API (로그인 Mock)
- 팀 관리 API (CRUD)
- 팀원 관리 API
- 모델 관리 API (CRUD, 활성/비활성)
- 할당량 설정 API
- 대시보드/통계 API (Mock 데이터)

### 4. 문서
- API 명세서 (Swagger UI)
- 프로젝트 README

---

## 백엔드 MVP 핵심 지표 (KPI)

| 지표 | 목표 | 측정 주기 |
|------|------|-----------|
| API 개발 완료도 | MVP 범위 내 100% | 개발 완료 시 |
| Mock 데이터 완성도 | 필요 데이터 100% | 개발 완료 시 |
| API 명세서 완성도 | 100% | 개발 완료 시 |
| Swagger UI 가동 | 성공 | 개발 완료 시 |
| 기간 준수 | 5일 (0.5 M/M) | 프로젝트 종료 시 |

---

## 백엔드 MVP 리스크 및 대응

| 리스크 ID | 리스크 내용 | 영향도 | 대응 계획 |
|-----------|-------------|--------|-----------|
| BE-MVP-R-01 | 기간 부족 | 높음 | API 최소화, 기본 CRUD 위주 구현 |
| BE-MVP-R-02 | Mock 데이터 복잡도 증가 | 낮음 | 간단한 JSON/SQL 구조 유지 |
| BE-MVP-R-03 | H2 DB 제약 사항 | 낮음 | In-Memory 특성 고려한 데이터 설계 |
| BE-MVP-R-04 | API 명세서 미작성 | 중간 | Swagger 설정 우선순위 높임 |

---

## 백엔드 MVP 검토 체크리스트

### 프로젝트 구조 검토
- [ ] Spring Boot 프로젝트가 정상적으로 초기화되었는가?
- [ ] H2 In-Memory DB가 설정되었는가?
- [ ] Mock 데이터가 로드되는가?

### API 품질 검토
- [ ] 모든 MVP 범위 API가 구현되었는가?
- [ ] API 요청/응답이 정상적인가?
- [ ] 에러 처리가 적절한가?
- [ ] Swagger UI에서 API 테스트가 가능한가?

### 데이터 구조 검토
- [ ] 엔티티가 적절하게 설계되었는가?
- [ ] Mock 데이터가 충분한가?
- [ ] 데이터 간 관계가 올바른가?

### 문서 검토
- [ ] Swagger UI가 제공되는가?
- [ ] API 명세가 명확한가?
- [ ] README가 작성되었는가?

---

## 백엔드 MVP 기술 스택

| 카테고리 | 기술 | 비고 |
|---------|------|------|
| 언어 | Java 17 or 21 | |
| 프레임워크 | Spring Boot 3.x | |
| DB | H2 (In-Memory) | PostgreSQL/MySQL 대체 |
| API 문서 | SpringDoc OpenAPI (Swagger) | |
| 빌드 도구 | Gradle | |
| 테스트 | JUnit 5 | 기본 단위 테스트만 |

## 백엔드 MVP 구성 패턴

```
src/main/java/com/example/llm/
├── controller/
│   ├── AuthController.java
│   ├── TeamController.java
│   ├── TeamMemberController.java
│   ├── ModelController.java
│   └── DashboardController.java
├── service/
│   ├── AuthService.java
│   ├── TeamService.java
│   ├── ModelService.java
│   └── DashboardService.java
├── entity/
│   ├── User.java
│   ├── Team.java
│   ├── TeamMember.java
│   ├── LLMModel.java
│   └── Usage.java
├── repository/
│   ├── UserRepository.java
│   ├── TeamRepository.java
│   ├── TeamMemberRepository.java
│   ├── ModelRepository.java
│   └── UsageRepository.java
└── config/
    ├── SwaggerConfig.java
    └── SecurityConfig.java (Mock)
```

---

*문서 작성일: 2026년 1월 8일*
*문서 버전: 1.0*
*작성자: 백엔드 개발자*
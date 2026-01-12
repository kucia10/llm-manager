# 프론트엔드(F/E) MVP WBS (Work Breakdown Structure)

## 역할 개요

- **담당자:** 프론트엔드 개발자 (1명)
- **기간:** 5일 (0.5 M/M)
- **총 M/M:** 0.5 M/M
- **주요 책임:** React.js 기반 UI 개발, Mock 데이터 활용, 필수 컴포넌트 개발

---

## 프론트엔드 MVP 제약 조건

1. **실제 B/E API 연동 없음:** Mock 데이터 활용 (json-server 또는 로컬 JSON 파일)
2. **복잡한 상태 관리 제외:** React Context API 또는 단순 useState 활용
3. **반응형 웹 최소 구현:** 데스크톱 위주, 기본 모바일 대응만
4. **다국어 지원 제외:** 한국어만 지원
5. **고급 UI/UX 제외:** 애니메이션, 트랜지션 등 최소화
6. **테스트 커버리지:** 기본 수준 (단순 동작 확인)

---

## 프론트엔드 담당 작업 일정 (MVP 기간)

| Phase | 기간 | 주요 작업 | 소요 M/M |
|-------|------|----------|----------|
| Phase 1 | Day 3 | 프로젝트 초기화 및 기본 설정 | 0.1 |
| Phase 2 | Day 4-5 | 공통 컴포넌트 및 핵심 화면 개발 | 0.25 |
| Phase 3 | Day 6-7 | 추가 화면 및 Mock 데이터 연동 | 0.15 |
| **합계** | **5일** | | **0.5** |

---

## 상세 WBS (Task 단위)

### WBS-FE-MVP-01: 프로젝트 초기화 및 기본 설정
**기간:** Day 3
**담당:** F/E 개발자
**소요 M/M:** 0.1

| Task ID | 작업 내용 | 상세 내용 | 산출물 |
|---------|----------|----------|--------|
| FE-MVP-01-01 | React 프로젝트 초기화 | Create React App 또는 Vite로 프로젝트 생성 | 프로젝트 구조 |
| FE-MVP-01-02 | 폴더 구조 설정 | pages, components, services, hooks 구조 설정 | 폴더 구조 |
| FE-MVP-01-03 | 라우팅 설정 | React Router 설치 및 기본 라우팅 구성 | 라우팅 설정 |
| FE-MVP-01-04 | 상태 관리 기본 설정 | React Context API 또는 Zustand 기본 설정 | 상태 관리 설정 |
| FE-MVP-01-05 | 기본 스타일 설정 | CSS Reset, 기본 컬러, 폰트 설정 | 기본 스타일 |

---

### WBS-FE-MVP-02: 공통 컴포넌트 개발
**기간:** Day 3-4
**담당:** F/E 개발자
**소요 M/M:** 0.08

| Task ID | 작업 내용 | 상세 내용 | 산출물 |
|---------|----------|----------|--------|
| FE-MVP-02-01 | 버튼 컴포넌트 개발 | 기본 버튼, 다양한 스타일 | Button 컴포넌트 |
| FE-MVP-02-02 | 폼 컴포넌트 개발 | 입력폼, 셀렉트, 체크박스 기본 구현 | Form 컴포넌트 |
| FE-MVP-02-03 | 카드 컴포넌트 개발 | 기본 카드, 대시보드 카드 | Card 컴포넌트 |
| FE-MVP-02-04 | 테이블 컴포넌트 개발 | 기본 데이터 테이블 | Table 컴포넌트 |
| FE-MVP-02-05 | 알림/모달 컴포넌트 개발 | 기본 알림, 모달 | Notification/Modal 컴포넌트 |
| FE-MVP-02-06 | 로딩 컴포넌트 개발 | 기본 로딩 스피너 | Loading 컴포넌트 |

**참고:**
- 복잡한 컴포넌트 기능 제외
- 기본 스타일만 구현, variant 최소화

---

### WBS-FE-MVP-03: 인증 화면 개발 (Mock)
**기간:** Day 4
**담당:** F/E 개발자
**관련 US:** US-01
**소요 M/M:** 0.05

| Task ID | 작업 내용 | 상세 내용 | 산출물 |
|---------|----------|----------|--------|
| FE-MVP-03-01 | 로그인 화면 개발 | 로그인 폼, Mock 로그인 로직 | 로그인 화면 컴포넌트 |
| FE-MVP-03-02 | 인증 상태 관리 구현 | 로그인 상태, 토큰 관리 (Mock) | 인증 상태 관리 |
| FE-MVP-03-03 | 라우트 보호 구현 | 인증된 사용자만 접근 가능하도록 설정 | 라우트 보호 |

**참고:**
- 실제 OAuth2.0 구현 없이 하드코딩된 로그인
- 토큰은 localStorage에 단순 저장
- MFA 제외

---

### WBS-FE-MVP-04: 팀 관리 화면 개발
**기간:** Day 4-5
**담당:** F/E 개발자
**관련 US:** US-04, US-05
**소요 M/M:** 0.08

| Task ID | 작업 내용 | 상세 내용 | 산출물 |
|---------|----------|----------|--------|
| FE-MVP-04-01 | 팀 목록 화면 개발 | 팀 리스트, 기본 검색/필터 | 팀 목록 화면 컴포넌트 |
| FE-MVP-04-02 | 팀 생성/수정 화면 개발 | 팀 정보 입력 폼 | 팀 생성/수정 화면 컴포넌트 |
| FE-MVP-04-03 | 팀 상세 화면 개발 | 팀 정보, 할당량 표시 | 팀 상세 화면 컴포넌트 |
| FE-MVP-04-04 | 팀원 관리 화면 개발 | 팀원 추가, 삭제, 역할 지정 | 팀원 관리 화면 컴포넌트 |
| FE-MVP-04-05 | 팀 관리 Mock 데이터 연동 | 로컬 JSON 데이터 활용 | 팀 관리 Mock 연동 |

**참고:**
- 검색/필터는 기본 구현만
- 복잡한 팀원 이동 기능 제외

---

### WBS-FE-MVP-05: LLM 모델 관리 화면 개발
**기간:** Day 5-6
**담당:** F/E 개발자
**관련 US:** US-06, US-07
**소요 M/M:** 0.07

| Task ID | 작업 내용 | 상세 내용 | 산출물 |
|---------|----------|----------|--------|
| FE-MVP-05-01 | 모델 목록 화면 개발 | 등록된 모델 리스트 | 모델 목록 화면 컴포넌트 |
| FE-MVP-05-02 | 모델 등록 화면 개발 | 모델 API, 비용, 파라미터 입력 폼 | 모델 등록 화면 컴포넌트 |
| FE-MVP-05-03 | 모델 상세 화면 개발 | 모델 정보, 기본 사용 통계 표시 | 모델 상세 화면 컴포넌트 |
| FE-MVP-05-04 | 모델 수정/삭제 화면 개발 | 모델 정보 수정, 삭제 확인 | 모델 수정/삭제 화면 컴포넌트 |
| FE-MVP-05-05 | 모델 활성/비활성 UI 개발 | 모델 상태 토글 | 모델 상태 UI 컴포넌트 |
| FE-MVP-05-06 | 모델 관리 Mock 데이터 연동 | 로컬 JSON 데이터 활용 | 모델 관리 Mock 연동 |

**참고:**
- 실제 모델 연결 테스트 제외
- 사용 통계는 Mock 데이터만 표시

---

### WBS-FE-MVP-06: 사용량 및 대시보드 화면 개발
**기간:** Day 6-7
**담당:** F/E 개발자
**관련 US:** US-08, US-09, US-10, US-13
**소요 M/M:** 0.07

| Task ID | 작업 내용 | 상세 내용 | 산출물 |
|---------|----------|----------|--------|
| FE-MVP-06-01 | 할당량 설정 화면 개발 | 팀/개인별 할당량 설정 폼 | 할당량 설정 화면 컴포넌트 |
| FE-MVP-06-02 | 종합 대시보드 메인 화면 개발 | 총 사용량, 활성 사용자, 기본 차트 | 종합 대시보드 화면 컴포넌트 |
| FE-MVP-06-03 | 차트 라이브러리 설정 및 기본 차트 | Chart.js 또는 Recharts 기본 설정 | 차트 라이브러리 설정 |
| FE-MVP-06-04 | 기본 차트 컴포넌트 개발 | 막대 차트, 파이 차트 기본 구현 | 차트 컴포넌트 |
| FE-MVP-06-05 | 대시보드 Mock 데이터 연동 | 로컬 JSON 데이터 활용 | 대시보드 Mock 연동 |

**참고:**
- 실시간 사용량 모니터링 없음 (Mock 데이터만 표시)
- WebSocket 제외
- 기본 차트만 구현, 고급 차트 제외

---

### WBS-FE-MVP-07: Mock 데이터 설정 및 연동
**기간:** Day 7
**담당:** F/E 개발자
**소요 M/M:** 0.05

| Task ID | 작업 내용 | 상세 내용 | 산출물 |
|---------|----------|----------|--------|
| FE-MVP-07-01 | json-server 설정 | json-server 설치 및 설정 | json-server 설정 |
| FE-MVP-07-02 | Mock 데이터 파일 작성 | users, teams, models, usage 등 Mock 데이터 | Mock 데이터 JSON 파일 |
| FE-MVP-07-03 | API 클라이언트 설정 | Axios 또는 Fetch 기본 설정, json-server 연동 | API 클라이언트 |
| FE-MVP-07-04 | Mock 데이터 연동 테스트 | 각 화면에서 Mock 데이터 연동 확인 | 연동 테스트 결과 |

**참고:**
- json-server 사용 권장 (또는 로컬 JSON 파일 직접 import)
- 실제 B/E API 연동 없음

---

### WBS-FE-MVP-08: 기본 UI/UX 개선
**기간:** Day 7
**담당:** F/E 개발자
**소요 M/M:** 0.03

| Task ID | 작업 내용 | 상세 내용 | 산출물 |
|---------|----------|----------|--------|
| FE-MVP-08-01 | 기본 반응형 웹 구현 | 기본 미디어 쿼리, 모바일 대응 | 반응형 CSS |
| FE-MVP-08-02 | 네비게이션 구현 | 기본 상단 네비게이션 | 네비게이션 컴포넌트 |
| FE-MVP-08-03 | 에러 처리 및 로딩 상태 | 기본 에러 메시지, 로딩 스피너 적용 | 에러 처리 로직 |

**참고:**
- 반응형 웹은 기본 구현만 (주요 화면 위주)
- 모바일 햄버거 메뉴 제외 가능
- 에러 처리는 기본 수준만

---

## 프론트엔드 MVP 산출물 목록

### 1. 프로젝트 구조 및 설정
- React 프로젝트 구조
- 라우팅 설정
- 상태 관리 설정 (React Context API)
- 기본 스타일 (CSS Reset, 컬러, 폰트)
- json-server 설정

### 2. 공통 컴포넌트
- Button 컴포넌트
- Form 컴포넌트
- Card 컴포넌트
- Table 컴포넌트
- Notification/Modal 컴포넌트
- Loading 컴포넌트

### 3. 화면별 컴포넌트
- 인증 화면 (로그인)
- 팀 관리 화면 (목록, 생성/수정, 상세, 팀원 관리)
- LLM 모델 관리 화면 (목록, 등록, 상세, 수정/삭제)
- 사용량/할당량 설정 화면
- 대시보드 화면 (종합 대시보드, 기본 차트)

### 4. Mock 데이터
- users.json (사용자 Mock 데이터)
- teams.json (팀 Mock 데이터)
- teamMembers.json (팀원 Mock 데이터)
- models.json (모델 Mock 데이터)
- usage.json (사용량 Mock 데이터)
- dashboard.json (대시보드 Mock 데이터)

### 5. 문서
- 프로젝트 README
- json-server 실행 가이드

---

## 프론트엔드 MVP 핵심 지표 (KPI)

| 지표 | 목표 | 측정 주기 |
|------|------|-----------|
| 화면 개발 완료도 | MVP 범위 내 100% | 개발 완료 시 |
| Mock 데이터 완성도 | 필요 데이터 100% | 개발 완료 시 |
| 기본 반응형 웹 | 주요 화면 80% | 개발 완료 시 |
| 기간 준수 | 5일 (0.5 M/M) | 프로젝트 종료 시 |

---

## 프론트엔드 MVP 리스크 및 대응

| 리스크 ID | 리스크 내용 | 영향도 | 대응 계획 |
|-----------|-------------|--------|-----------|
| FE-MVP-R-01 | 기간 부족 | 높음 | 화면 최소화, 기본 UI 위주 구현 |
| FE-MVP-R-02 | Mock 데이터 복잡도 증가 | 낮음 | 간단한 JSON 구조 유지 |
| FE-MVP-R-03 | 반응형 웹 복잡성 | 낮음 | 기본 미디어 쿼리만 적용 |
| FE-MVP-R-04 | 상태 관리 복잡성 증가 | 낮음 | React Context API 기본 구현만 |

---

## 프론트엔드 MVP 검토 체크리스트

### 프로젝트 구조 검토
- [ ] React 프로젝트가 정상적으로 초기화되었는가?
- [ ] 라우팅이 설정되었는가?
- [ ] 상태 관리가 설정되었는가?
- [ ] json-server가 설정되었는가?

### 컴포넌트 품질 검토
- [ ] 공통 컴포넌트가 재사용 가능한가?
- [ ] 컴포넌트가 기본 기능만 포함하는가?
- [ ] Props/State가 적절하게 관리되는가?

### 화면 구현 검토
- [ ] 모든 MVP 범위 화면이 구현되었는가?
- [ ] Mock 데이터가 정상적으로 표시되는가?
- [ ] 기본 반응형 웹이 구현되었는가?
- [ ] 네비게이션이 작동하는가?

### Mock 데이터 검토
- [ ] 필요한 Mock 데이터가 모두 있는가?
- [ ] Mock 데이터 구조가 올바른가?
- [ ] json-server가 정상적으로 동작하는가?

---

## 프론트엔드 MVP 기술 스택

| 카테고리 | 기술 | 비고 |
|---------|------|------|
| 언어 | JavaScript (ES6+) | 또는 TypeScript |
| 프레임워크 | React 18+ | |
| 라우팅 | React Router 6+ | |
| 상태 관리 | React Context API | 또는 Zustand |
| 스타일 | CSS | 또는 Tailwind CSS (선택사항) |
| Mock API | json-server | 또는 로컬 JSON 파일 |
| HTTP 클라이언트 | Axios | |
| 차트 라이브러리 | Chart.js 또는 Recharts | 기본 차트만 |
| 빌드 도구 | Vite | 또는 CRA |

## 프론트엔드 MVP 구성 패턴

```
src/
├── components/
│   ├── common/
│   │   ├── Button.jsx
│   │   ├── Form.jsx
│   │   ├── Card.jsx
│   │   ├── Table.jsx
│   │   ├── Notification.jsx
│   │   ├── Modal.jsx
│   │   └── Loading.jsx
│   ├── layout/
│   │   ├── Header.jsx
│   │   └── Layout.jsx
│   └── pages/
│       ├── Login.jsx
│       ├── TeamList.jsx
│       ├── TeamDetail.jsx
│       ├── TeamCreate.jsx
│       ├── ModelList.jsx
│       ├── ModelDetail.jsx
│       ├── ModelCreate.jsx
│       ├── QuotaSettings.jsx
│       └── Dashboard.jsx
├── context/
│   └── AuthContext.jsx
├── services/
│   └── api.js (Axios 설정)
├── hooks/
│   └── useAuth.js
├── styles/
│   ├── reset.css
│   └── global.css
├── mock/
│   ├── users.json
│   ├── teams.json
│   ├── teamMembers.json
│   ├── models.json
│   ├── usage.json
│   └── dashboard.json
└── App.jsx
```

## json-server 설정 예시

```json
{
  "users": [
    {
      "id": 1,
      "username": "admin",
      "email": "admin@example.com",
      "role": "admin"
    },
    {
      "id": 2,
      "username": "user1",
      "email": "user1@example.com",
      "role": "user"
    }
  ],
  "teams": [
    {
      "id": 1,
      "name": "팀 A",
      "quota": 1000000,
      "usage": 500000
    },
    {
      "id": 2,
      "name": "팀 B",
      "quota": 800000,
      "usage": 400000
    }
  ],
  "models": [
    {
      "id": 1,
      "name": "GPT-4",
      "provider": "OpenAI",
      "costPerToken": 0.00003,
      "isActive": true
    },
    {
      "id": 2,
      "name": "Claude-3",
      "provider": "Anthropic",
      "costPerToken": 0.000025,
      "isActive": true
    }
  ],
  "usage": [
    {
      "id": 1,
      "teamId": 1,
      "modelId": 1,
      "tokens": 1000,
      "cost": 0.03,
      "date": "2026-01-01"
    }
  ],
  "dashboard": {
    "totalUsage": 900000,
    "activeUsers": 5,
    "teams": 2,
    "models": 2
  }
}
```

---

*문서 작성일: 2026년 1월 8일*
*문서 버전: 1.0*
*작성자: 프론트엔드 개발자*
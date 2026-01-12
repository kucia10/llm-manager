#!/bin/bash

# 전체 애플리케이션 실행 스크립트
# - 백엔드 (Spring Boot): 포트 8080
# - 프론트엔드 (Vite): 포트 5173

set -e

echo "══════════════════════════════════════════════════════════"
echo "  🚀 전체 애플리케이션 시작 중..."
echo "══════════════════════════════════════════════════════════"
echo ""

# 프로젝트 루트 디렉토리 저장
PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"

# 임시 로그 파일 생성 (절대 경로)
LOG_DIR="$PROJECT_ROOT/logs"
mkdir -p "$LOG_DIR"

# 프로젝트 루트 디렉토리로 이동
cd "$PROJECT_ROOT"

# PIDs 저장 배열
PIDS=()

# cleanup 함수
cleanup() {
    echo ""
    echo "══════════════════════════════════════════════════════════"
    echo "  🛑 모든 서비스 종료 중..."
    echo "══════════════════════════════════════════════════════════"
    
    # 백엔드 종료
    if [ -n "$BACKEND_PID" ]; then
        kill $BACKEND_PID 2>/dev/null && echo "   ✅ Spring Boot 서버 종료됨 (PID: $BACKEND_PID)"
    fi
    
    # 프론트엔드 종료
    if [ -n "$FRONTEND_PID" ]; then
        kill $FRONTEND_PID 2>/dev/null && echo "   ✅ Vite 개발 서버 종료됨 (PID: $FRONTEND_PID)"
    fi
    
    # Mock API 종료 (실행 중인 경우)
    pkill -f "json-server" 2>/dev/null && echo "   ✅ Mock API 서버 종료됨"
    
    echo ""
    echo "🗑️  로그 파일: $LOG_DIR/"
    echo "✅ 모든 서비스가 종료되었습니다."
    exit 0
}

# Ctrl+C 시 cleanup 함수 실행
trap cleanup INT TERM

# ══════════════════════════════════════════════════════════
# 백엔드 서버 시작 (Spring Boot)
# ══════════════════════════════════════════════════════════
echo "📦 백엔드 서버 시작 중..."
cd mvp-server

# Gradle wrapper 존재 확인
if [ ! -f "gradlew" ]; then
    echo "❌ gradlew 파일을 찾을 수 없습니다."
    exit 1
fi

# Gradle wrapper 실행 권한 확인
if [ ! -x "gradlew" ]; then
    echo "🔧 gradlew에 실행 권한 부여 중..."
    chmod +x gradlew
fi

# Spring Boot 서버 백그라운드 실행
echo "   Spring Boot 서버 시작 중 (포트 8080)..."
./gradlew bootRun --stacktrace > "$LOG_DIR/backend.log" 2>&1 &
BACKEND_PID=$!
PIDS+=($BACKEND_PID)
echo "   ✅ Spring Boot 서버 시작됨 (PID: $BACKEND_PID)"
echo "   📄 로그 파일: $LOG_DIR/backend.log"

cd ..

# Spring Boot 서버가 시작될 때까지 대기 (최대 60초)
echo "   ⏳ 백엔드 서버 대기 중..."
TIMEOUT=60
ELAPSED=0
while [ $ELAPSED -lt $TIMEOUT ]; do
    if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1 || \
       curl -s http://localhost:8080/swagger-ui.html > /dev/null 2>&1; then
        echo "   ✅ 백엔드 서버가 준비되었습니다!"
        break
    fi
    sleep 2
    ELAPSED=$((ELAPSED + 2))
    
    # 프로세스가 여전히 실행 중인지 확인
    if ! kill -0 $BACKEND_PID 2>/dev/null; then
        echo "   ❌ 백엔드 서버가 비정상 종료되었습니다."
        echo "   📄 로그 확인:"
        tail -20 "$LOG_DIR/backend.log"
        exit 1
    fi
    
    if [ $ELAPSED -eq 30 ]; then
        echo "   ⚠️  백엔드 서버 시작에 시간이 소요되고 있습니다..."
    fi
done

if [ $ELAPSED -ge $TIMEOUT ]; then
    echo "   ❌ 백엔드 서버 시작 시간 초과"
    echo "   📄 로그 확인:"
    tail -20 "$LOG_DIR/backend.log"
    exit 1
fi

echo ""

# ══════════════════════════════════════════════════════════
# 프론트엔드 서버 시작 (Vite)
# ══════════════════════════════════════════════════════════
echo "🎨 프론트엔드 서버 시작 중..."
cd mvp-client

# npm 의존성 설치 확인
if [ ! -d "node_modules" ]; then
    echo "📦 npm 의존성 설치 중..."
    npm install
fi

# Vite 개발 서버 백그라운드 실행
echo "   Vite 개발 서버 시작 중 (포트 5173)..."
npm run dev > "$LOG_DIR/frontend.log" 2>&1 &
FRONTEND_PID=$!
PIDS+=($FRONTEND_PID)
echo "   ✅ Vite 개발 서버 시작됨 (PID: $FRONTEND_PID)"
echo "   📄 로그 파일: $LOG_DIR/frontend.log"

cd ..

# Vite 서버가 시작될 때까지 대기 (최대 30초)
echo "   ⏳ 프론트엔드 서버 대기 중..."
TIMEOUT=30
ELAPSED=0
while [ $ELAPSED -lt $TIMEOUT ]; do
    if curl -s http://localhost:5173 > /dev/null 2>&1; then
        echo "   ✅ 프론트엔드 서버가 준비되었습니다!"
        break
    fi
    sleep 2
    ELAPSED=$((ELAPSED + 2))
    
    # 프로세스가 여전히 실행 중인지 확인
    if ! kill -0 $FRONTEND_PID 2>/dev/null; then
        echo "   ❌ 프론트엔드 서버가 비정상 종료되었습니다."
        echo "   📄 로그 확인:"
        tail -20 "$LOG_DIR/frontend.log"
        exit 1
    fi
done

if [ $ELAPSED -ge $TIMEOUT ]; then
    echo "   ❌ 프론트엔드 서버 시작 시간 초과"
    echo "   📄 로그 확인:"
    tail -20 "$LOG_DIR/frontend.log"
    exit 1
fi

echo ""
echo "══════════════════════════════════════════════════════════"
echo "  ✅ 모든 서비스 시작 완료!"
echo "══════════════════════════════════════════════════════════"
echo ""
echo "  🌐 프론트엔드 (Vite):  http://localhost:5173"
echo "  🚀 백엔드 (Spring Boot): http://localhost:8080"
echo "  📚 API 문서 (Swagger):   http://localhost:8080/swagger-ui.html"
echo "  💾 H2 콘솔:             http://localhost:8080/h2-console"
echo ""
echo "  📄 로그 파일:"
echo "     - 백엔드: $LOG_DIR/backend.log"
echo "     - 프론트엔드: $LOG_DIR/frontend.log"
echo ""
echo "  📊 로그 실시간 확인 명령어:"
echo "     - 백엔드: tail -f $LOG_DIR/backend.log"
echo "     - 프론트엔드: tail -f $LOG_DIR/frontend.log"
echo "     - 전체: tail -f $LOG_DIR/*.log"
echo ""
echo "  ⏹️  종료하려면 Ctrl+C를 누르세요."
echo "══════════════════════════════════════════════════════════"
echo ""

# 백그라운드 프로세스 모니터링
wait
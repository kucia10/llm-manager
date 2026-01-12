# AGENTS.md

This file provides guidance to agents when working with code in this repository.

## Project Structure
- **mvc-client/**: React + Vite frontend
- **mvp-server/**: Spring Boot 3.2 + Java 17 backend

## Commands (Frontend - mvc-client)

### Running Tests
```bash
# Run all tests once (recommended for CI/CD)
npm run test -- --run

# Run specific test file
npm run test -- src/test/components/pages/Dashboard.test.jsx

# Watch mode (auto-rerun on changes)
npm run test

# Test with UI
npm run test:ui

# Coverage report
npm run test:coverage
```

### Development
```bash
# Start dev server with Mock API (recommended)
./scripts/dev.sh

# Alternative: Vite only
npm run dev

# Mock API only (port 3000)
npm run mock
```

### Build & Lint
```bash
# Production build
npm run build

# Preview production build
npm run preview

# Lint code
npm run lint
```

## Critical Patterns

### API Client Usage
ALWAYS use `apiClient` from `src/utils/apiClient.js` for HTTP requests:
- Automatically adds Bearer token from localStorage
- Handles 401 errors by redirecting to /login
- Base URL configurable via VITE_API_BASE_URL env var

### Service Pattern
All services (in `src/services/`) must:
1. Import `apiClient` from `../utils/apiClient`
2. Export object with async methods
3. Return data directly (apiClient already unwraps response)

### Authentication
- Token stored in localStorage under 'token' key
- AuthContext in `src/context/AuthContext.jsx` manages auth state
- useAuth hook available at both `src/hooks/useAuth.js` and `src/context/AuthContext.jsx` (same implementation)
- Protected routes use `ProtectedRoute` component in App.jsx

### Testing Requirements
- Test files must be in `src/test/` directory (not parallel to source)
- Setup file: `src/test/setup.js` configures localStorage mock
- Wrap components with `BrowserRouter` and `AuthProvider` in tests
- Mock services with `vi.mock()` using exact file paths
- Use Korean for test descriptions (e.g., `it('로딩 상태일 때...')`)

### Environment Variables
Frontend (mvc-client):
- `VITE_API_BASE_URL=http://localhost:3000` (default)

Backend (mvp-server):
- Uses H2 in-memory database (dev only)
- JWT secret: `mvp-mock-secret-key-for-development-only` (dev only)

## Port Information
- Mock API Server: 3000 (json-server)
- Vite Dev Server: 5173
- Spring Boot Server: 8080
- H2 Console: 8080/h2-console

## Backend Commands (mvp-server)
```bash
# Run Spring Boot app
./gradlew bootRun

# Run tests
./gradlew test

# Build JAR
./gradlew bootJar
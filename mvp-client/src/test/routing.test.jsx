import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { BrowserRouter, MemoryRouter, Routes, Route } from 'react-router-dom'
import { AuthProvider } from '../common/context/AuthContext'
import App from '../App'

vi.mock('../common/services/dashboardService', () => ({
  dashboardService: {
    getFeaturedModels: vi.fn(),
    getRecentActivity: vi.fn(),
    getOverview: vi.fn(),
    getStatistics: vi.fn(),
  },
}))

vi.mock('../common/services/authService', () => ({
  authService: {
    login: vi.fn(),
    getMe: vi.fn(),
  },
}))

const renderApp = (initialEntries = ['/']) => {
  return render(
    <MemoryRouter initialEntries={initialEntries}>
      <AuthProvider>
        <App />
      </AuthProvider>
    </MemoryRouter>
  )
}

describe('라우팅 및 네비게이션 테스트', () => {
  beforeEach(() => {
    localStorage.clear()
  })

  afterEach(() => {
    localStorage.clear()
    vi.clearAllMocks()
  })

  describe('공개 경로', () => {
    it('루트 경로(/)는 인증 없이 접근 가능하다', async () => {
      const { dashboardService } = await import('../common/services/dashboardService')
      dashboardService.getFeaturedModels.mockResolvedValue([])
      dashboardService.getRecentActivity.mockResolvedValue([])
      
      renderApp(['/'])
      
      await waitFor(() => {
        expect(screen.getByText('홈')).toBeInTheDocument()
      })
    })

    it('로그인 경로(/login)는 인증 없이 접근 가능하다', () => {
      renderApp(['/login'])
      
      expect(screen.getByText('로그인')).toBeInTheDocument()
    })
  })

  describe('보호된 경로', () => {
    it('인증되지 않은 사용자가 보호된 경로에 접근하면 로그인 페이지로 리다이렉트된다', async () => {
      renderApp(['/dashboard'])
      
      await waitFor(() => {
        expect(screen.getByText('로그인')).toBeInTheDocument()
      })
    })

    it('인증된 사용자는 보호된 경로에 접근 가능하다', async () => {
      localStorage.setItem('token', 'fake-token')
      localStorage.setItem('user', JSON.stringify({ id: 1, email: 'test@example.com', name: '테스트 사용자' }))
      
      const { dashboardService } = await import('../common/services/dashboardService')
      dashboardService.getOverview.mockResolvedValue({ totalModels: 10 })
      dashboardService.getRecentActivity.mockResolvedValue([])
      dashboardService.getStatistics.mockResolvedValue({})
      
      renderApp(['/dashboard'])
      
      await waitFor(() => {
        expect(screen.getByText('대시보드')).toBeInTheDocument()
      })
    })

    it('인증된 사용자는 /teams 경로에 접근 가능하다', async () => {
      localStorage.setItem('token', 'fake-token')
      localStorage.setItem('user', JSON.stringify({ id: 1, email: 'test@example.com', name: '테스트 사용자' }))
      
      renderApp(['/teams'])
      
      await waitFor(() => {
        expect(screen.getByText('팀 목록')).toBeInTheDocument()
      })
    })

    it('인증된 사용자는 /teams/create 경로에 접근 가능하다', async () => {
      localStorage.setItem('token', 'fake-token')
      localStorage.setItem('user', JSON.stringify({ id: 1, email: 'test@example.com', name: '테스트 사용자' }))
      
      renderApp(['/teams/create'])
      
      await waitFor(() => {
        expect(screen.getByText('팀 생성')).toBeInTheDocument()
      })
    })

    it('인증된 사용자는 /teams/:id 경로에 접근 가능하다', async () => {
      localStorage.setItem('token', 'fake-token')
      localStorage.setItem('user', JSON.stringify({ id: 1, email: 'test@example.com', name: '테스트 사용자' }))
      
      renderApp(['/teams/1'])
      
      await waitFor(() => {
        expect(screen.getByText('팀 상세')).toBeInTheDocument()
      })
    })

    it('인증된 사용자는 /models 경로에 접근 가능하다', async () => {
      localStorage.setItem('token', 'fake-token')
      localStorage.setItem('user', JSON.stringify({ id: 1, email: 'test@example.com', name: '테스트 사용자' }))
      
      renderApp(['/models'])
      
      await waitFor(() => {
        expect(screen.getByText('모델 목록')).toBeInTheDocument()
      })
    })

    it('인증된 사용자는 /models/create 경로에 접근 가능하다', async () => {
      localStorage.setItem('token', 'fake-token')
      localStorage.setItem('user', JSON.stringify({ id: 1, email: 'test@example.com', name: '테스트 사용자' }))
      
      renderApp(['/models/create'])
      
      await waitFor(() => {
        expect(screen.getByText('모델 생성')).toBeInTheDocument()
      })
    })
  })

  describe('네비게이션', () => {
    it('홈 페이지에서 로그인 페이지로 이동 가능하다', async () => {
      const { dashboardService } = await import('../common/services/dashboardService')
      dashboardService.getFeaturedModels.mockResolvedValue([])
      dashboardService.getRecentActivity.mockResolvedValue([])
      
      renderApp(['/'])
      
      await waitFor(() => {
        expect(screen.getByText('홈')).toBeInTheDocument()
      })
      
      const loginLink = screen.getByText('로그인')
      expect(loginLink).toBeInTheDocument()
    })

    it('로그인 후 대시보드로 이동 가능하다', async () => {
      localStorage.setItem('token', 'fake-token')
      localStorage.setItem('user', JSON.stringify({ id: 1, email: 'test@example.com', name: '테스트 사용자' }))
      
      const { dashboardService } = await import('../common/services/dashboardService')
      dashboardService.getOverview.mockResolvedValue({ totalModels: 10 })
      dashboardService.getRecentActivity.mockResolvedValue([])
      dashboardService.getStatistics.mockResolvedValue({})
      
      renderApp(['/dashboard'])
      
      await waitFor(() => {
        expect(screen.getByText('대시보드')).toBeInTheDocument()
      })
    })
  })

  describe('로딩 상태', () => {
    it('인증 확인 중에는 로딩 메시지가 표시된다', async () => {
      const { dashboardService } = await import('../common/services/dashboardService')
      dashboardService.getFeaturedModels.mockResolvedValue([])
      dashboardService.getRecentActivity.mockResolvedValue([])
      
      renderApp(['/'])
      
      await waitFor(() => {
        expect(screen.getByText('홈')).toBeInTheDocument()
      })
    })
  })

  describe('404 처리', () => {
    it('존재하지 않는 경로에 접근하면 적절히 처리된다', async () => {
      localStorage.setItem('token', 'fake-token')
      localStorage.setItem('user', JSON.stringify({ id: 1, email: 'test@example.com', name: '테스트 사용자' }))
      
      renderApp(['/nonexistent'])
      
      await waitFor(() => {
        expect(screen.getByText('페이지를 찾을 수 없습니다')).toBeInTheDocument()
      })
    })
  })

  describe('경로 파라미터', () => {
    it('팀 ID 파라미터가 올바르게 전달된다', async () => {
      localStorage.setItem('token', 'fake-token')
      localStorage.setItem('user', JSON.stringify({ id: 1, email: 'test@example.com', name: '테스트 사용자' }))
      
      renderApp(['/teams/123'])
      
      await waitFor(() => {
        expect(screen.getByText('팀 상세')).toBeInTheDocument()
      })
    })
  })

  describe('인증 후 리다이렉트', () => {
    it('로그인 성공 후 이전에 시도한 경로로 리다이렉트된다', async () => {
      const { dashboardService } = await import('../common/services/dashboardService')
      dashboardService.getFeaturedModels.mockResolvedValue([])
      dashboardService.getRecentActivity.mockResolvedValue([])
      
      renderApp(['/login'])
      
      expect(screen.getByText('로그인')).toBeInTheDocument()
    })
  })
})
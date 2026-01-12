import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { BrowserRouter } from 'react-router-dom'
import { AuthProvider } from '../common/context/AuthContext'
import Home from '../../pages/Home'

vi.mock('../../common/services/dashboardService', () => ({
  dashboardService: {
    getFeaturedModels: vi.fn(),
    getRecentActivity: vi.fn(),
  },
}))

const renderWithProviders = (component) => {
  return render(
    <BrowserRouter>
      <AuthProvider>
        {component}
      </AuthProvider>
    </BrowserRouter>
  )
}

describe('Home 페이지', () => {
  beforeEach(() => {
    localStorage.setItem('token', 'fake-token')
    localStorage.setItem('user', JSON.stringify({ id: 1, email: 'test@example.com', name: '테스트 사용자' }))
  })

  it('홈 페이지가 올바르게 렌더링된다', async () => {
    const { dashboardService } = await import('../../common/services/dashboardService')
    dashboardService.getFeaturedModels.mockResolvedValue([])
    dashboardService.getRecentActivity.mockResolvedValue([])
    
    renderWithProviders(<Home />)
    
    await waitFor(() => {
      expect(screen.getByText('홈')).toBeInTheDocument()
    })
  })

  it('인사말이 사용자 이름과 함께 표시된다', async () => {
    const { dashboardService } = await import('../../common/services/dashboardService')
    dashboardService.getFeaturedModels.mockResolvedValue([])
    dashboardService.getRecentActivity.mockResolvedValue([])
    
    renderWithProviders(<Home />)
    
    await waitFor(() => {
      expect(screen.getByText('안녕하세요, 테스트 사용자님!')).toBeInTheDocument()
    })
  })

  it('빠른 시작 메뉴가 표시된다', async () => {
    const { dashboardService } = await import('../../common/services/dashboardService')
    dashboardService.getFeaturedModels.mockResolvedValue([])
    dashboardService.getRecentActivity.mockResolvedValue([])
    
    renderWithProviders(<Home />)
    
    await waitFor(() => {
      expect(screen.getByText('빠른 시작')).toBeInTheDocument()
      expect(screen.getByText('모델 생성')).toBeInTheDocument()
      expect(screen.getByText('팀 생성')).toBeInTheDocument()
      expect(screen.getByText('대시보드')).toBeInTheDocument()
    })
  })

  it('빠른 시작 링크가 올바르게 작동한다', async () => {
    const { dashboardService } = await import('../../common/services/dashboardService')
    dashboardService.getFeaturedModels.mockResolvedValue([])
    dashboardService.getRecentActivity.mockResolvedValue([])
    
    renderWithProviders(<Home />)
    
    await waitFor(() => {
      const modelCreateLink = screen.getByText('모델 생성')
      expect(modelCreateLink.closest('a')).toHaveAttribute('href', '/models/create')
      
      const teamCreateLink = screen.getByText('팀 생성')
      expect(teamCreateLink.closest('a')).toHaveAttribute('href', '/teams/create')
      
      const dashboardLink = screen.getByText('대시보드')
      expect(dashboardLink.closest('a')).toHaveAttribute('href', '/dashboard')
    })
  })

  it('추천 모델이 표시된다', async () => {
    const { dashboardService } = await import('../../common/services/dashboardService')
    dashboardService.getFeaturedModels.mockResolvedValue([
      { id: 1, name: '모델 1', description: '설명 1', accuracy: 0.95 },
      { id: 2, name: '모델 2', description: '설명 2', accuracy: 0.88 },
    ])
    dashboardService.getRecentActivity.mockResolvedValue([])
    
    renderWithProviders(<Home />)
    
    await waitFor(() => {
      expect(screen.getByText('추천 모델')).toBeInTheDocument()
      expect(screen.getByText('모델 1')).toBeInTheDocument()
      expect(screen.getByText('모델 2')).toBeInTheDocument()
    })
  })

  it('최근 활동이 표시된다', async () => {
    const { dashboardService } = await import('../../common/services/dashboardService')
    dashboardService.getFeaturedModels.mockResolvedValue([])
    dashboardService.getRecentActivity.mockResolvedValue([
      { id: 1, type: 'model', message: '모델 생성됨', time: '10분 전' },
      { id: 2, type: 'team', message: '팀 생성됨', time: '1시간 전' },
    ])
    
    renderWithProviders(<Home />)
    
    await waitFor(() => {
      expect(screen.getByText('최근 활동')).toBeInTheDocument()
      expect(screen.getByText('모델 생성됨')).toBeInTheDocument()
      expect(screen.getByText('팀 생성됨')).toBeInTheDocument()
    })
  })

  it('로딩 중에는 스켈레톤이 표시된다', async () => {
    const { dashboardService } = await import('../../common/services/dashboardService')
    dashboardService.getFeaturedModels.mockImplementation(() => new Promise(() => {}))
    dashboardService.getRecentActivity.mockResolvedValue([])
    
    renderWithProviders(<Home />)
    
    expect(screen.getByTestId('loading-skeleton')).toBeInTheDocument()
  })

  it('데이터 로드 실패 시 에러 메시지가 표시된다', async () => {
    const { dashboardService } = await import('../../common/services/dashboardService')
    dashboardService.getFeaturedModels.mockRejectedValue(new Error('데이터를 불러오는 데 실패했습니다.'))
    dashboardService.getRecentActivity.mockResolvedValue([])
    
    renderWithProviders(<Home />)
    
    await waitFor(() => {
      const errorMessage = screen.getByText(/데이터를 불러오는 데 실패했습니다/i)
      expect(errorMessage).toBeInTheDocument()
    })
  })

  it('빈 상태가 올바르게 표시된다', async () => {
    const { dashboardService } = await import('../../common/services/dashboardService')
    dashboardService.getFeaturedModels.mockResolvedValue([])
    dashboardService.getRecentActivity.mockResolvedValue([])
    
    renderWithProviders(<Home />)
    
    await waitFor(() => {
      expect(screen.getByText(/추천 모델이 없습니다/i)).toBeInTheDocument()
      expect(screen.getByText(/활동이 없습니다/i)).toBeInTheDocument()
    })
  })

  it('모델 클릭 시 상세 페이지로 이동한다', async () => {
    const user = userEvent.setup()
    const { dashboardService } = await import('../../common/services/dashboardService')
    dashboardService.getFeaturedModels.mockResolvedValue([
      { id: 1, name: '모델 1', description: '설명 1', accuracy: 0.95 },
    ])
    dashboardService.getRecentActivity.mockResolvedValue([])
    
    renderWithProviders(<Home />)
    
    await waitFor(() => {
      const modelCard = screen.getByText('모델 1')
      expect(modelCard.closest('a')).toHaveAttribute('href', '/models/1')
    })
  })
})
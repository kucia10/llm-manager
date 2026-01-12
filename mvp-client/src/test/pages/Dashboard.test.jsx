import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { BrowserRouter } from 'react-router-dom'
import { AuthProvider } from '../common/context/AuthContext'
import Dashboard from '../../pages/Dashboard'

vi.mock('../../common/services/dashboardService', () => ({
  dashboardService: {
    getOverview: vi.fn(),
    getRecentActivity: vi.fn(),
    getStatistics: vi.fn(),
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

describe('Dashboard 페이지', () => {
  beforeEach(() => {
    localStorage.setItem('token', 'fake-token')
    localStorage.setItem('user', JSON.stringify({ id: 1, email: 'test@example.com', name: '테스트 사용자' }))
  })

  it('대시보드가 올바르게 렌더링된다', async () => {
    const { dashboardService } = await import('../../common/services/dashboardService')
    dashboardService.getOverview.mockResolvedValue({
      totalModels: 10,
      totalTeams: 5,
      recentActivity: 20,
      storageUsed: '2.5 GB',
    })
    dashboardService.getRecentActivity.mockResolvedValue([
      { id: 1, type: 'model', message: '모델 생성됨', time: '10분 전' },
      { id: 2, type: 'team', message: '팀 생성됨', time: '1시간 전' },
    ])
    dashboardService.getStatistics.mockResolvedValue({
      modelsCreated: 15,
      modelsDeployed: 8,
      teamsActive: 5,
      usersActive: 25,
    })
    
    renderWithProviders(<Dashboard />)
    
    await waitFor(() => {
      expect(screen.getByText('대시보드')).toBeInTheDocument()
    })
  })

  it('개요 카드들이 표시된다', async () => {
    const { dashboardService } = await import('../../common/services/dashboardService')
    dashboardService.getOverview.mockResolvedValue({
      totalModels: 10,
      totalTeams: 5,
      recentActivity: 20,
      storageUsed: '2.5 GB',
    })
    dashboardService.getRecentActivity.mockResolvedValue([])
    dashboardService.getStatistics.mockResolvedValue({})
    
    renderWithProviders(<Dashboard />)
    
    await waitFor(() => {
      expect(screen.getByText('전체 모델')).toBeInTheDocument()
      expect(screen.getByText('전체 팀')).toBeInTheDocument()
      expect(screen.getByText('최근 활동')).toBeInTheDocument()
      expect(screen.getByText('사용 저장 공간')).toBeInTheDocument()
    })
  })

  it('개요 데이터가 올바르게 표시된다', async () => {
    const { dashboardService } = await import('../../common/services/dashboardService')
    dashboardService.getOverview.mockResolvedValue({
      totalModels: 10,
      totalTeams: 5,
      recentActivity: 20,
      storageUsed: '2.5 GB',
    })
    dashboardService.getRecentActivity.mockResolvedValue([])
    dashboardService.getStatistics.mockResolvedValue({})
    
    renderWithProviders(<Dashboard />)
    
    await waitFor(() => {
      expect(screen.getByText('10')).toBeInTheDocument()
      expect(screen.getByText('5')).toBeInTheDocument()
      expect(screen.getByText('20')).toBeInTheDocument()
      expect(screen.getByText('2.5 GB')).toBeInTheDocument()
    })
  })

  it('최근 활동이 표시된다', async () => {
    const { dashboardService } = await import('../../common/services/dashboardService')
    dashboardService.getOverview.mockResolvedValue({})
    dashboardService.getRecentActivity.mockResolvedValue([
      { id: 1, type: 'model', message: '모델 생성됨', time: '10분 전' },
      { id: 2, type: 'team', message: '팀 생성됨', time: '1시간 전' },
    ])
    dashboardService.getStatistics.mockResolvedValue({})
    
    renderWithProviders(<Dashboard />)
    
    await waitFor(() => {
      expect(screen.getByText('최근 활동')).toBeInTheDocument()
      expect(screen.getByText('모델 생성됨')).toBeInTheDocument()
      expect(screen.getByText('팀 생성됨')).toBeInTheDocument()
      expect(screen.getByText('10분 전')).toBeInTheDocument()
      expect(screen.getByText('1시간 전')).toBeInTheDocument()
    })
  })

  it('통계 데이터가 올바르게 표시된다', async () => {
    const { dashboardService } = await import('../../common/services/dashboardService')
    dashboardService.getOverview.mockResolvedValue({})
    dashboardService.getRecentActivity.mockResolvedValue([])
    dashboardService.getStatistics.mockResolvedValue({
      modelsCreated: 15,
      modelsDeployed: 8,
      teamsActive: 5,
      usersActive: 25,
    })
    
    renderWithProviders(<Dashboard />)
    
    await waitFor(() => {
      expect(screen.getByText('통계')).toBeInTheDocument()
      expect(screen.getByText('15')).toBeInTheDocument()
      expect(screen.getByText('8')).toBeInTheDocument()
      expect(screen.getByText('5')).toBeInTheDocument()
      expect(screen.getByText('25')).toBeInTheDocument()
    })
  })

  it('로딩 중에는 스켈레톤이 표시된다', async () => {
    const { dashboardService } = await import('../../common/services/dashboardService')
    dashboardService.getOverview.mockImplementation(() => new Promise(() => {}))
    dashboardService.getRecentActivity.mockResolvedValue([])
    dashboardService.getStatistics.mockResolvedValue({})
    
    renderWithProviders(<Dashboard />)
    
    expect(screen.getByTestId('loading-skeleton')).toBeInTheDocument()
  })

  it('데이터 로드 실패 시 에러 메시지가 표시된다', async () => {
    const { dashboardService } = await import('../../common/services/dashboardService')
    dashboardService.getOverview.mockRejectedValue(new Error('데이터를 불러오는 데 실패했습니다.'))
    dashboardService.getRecentActivity.mockResolvedValue([])
    dashboardService.getStatistics.mockResolvedValue({})
    
    renderWithProviders(<Dashboard />)
    
    await waitFor(() => {
      const errorMessage = screen.getByText(/데이터를 불러오는 데 실패했습니다/i)
      expect(errorMessage).toBeInTheDocument()
    })
  })

  it('새로고침 버튼이 작동한다', async () => {
    const user = userEvent.setup()
    const { dashboardService } = await import('../../common/services/dashboardService')
    dashboardService.getOverview.mockResolvedValue({ totalModels: 10 })
    dashboardService.getRecentActivity.mockResolvedValue([])
    dashboardService.getStatistics.mockResolvedValue({})
    
    renderWithProviders(<Dashboard />)
    
    await waitFor(() => {
      expect(dashboardService.getOverview).toHaveBeenCalledTimes(1)
    })
    
    const refreshButton = screen.getByRole('button', { name: /새로고침/i })
    await user.click(refreshButton)
    
    await waitFor(() => {
      expect(dashboardService.getOverview).toHaveBeenCalledTimes(2)
    })
  })

  it('빈 상태가 올바르게 표시된다', async () => {
    const { dashboardService } = await import('../../common/services/dashboardService')
    dashboardService.getOverview.mockResolvedValue({
      totalModels: 0,
      totalTeams: 0,
      recentActivity: 0,
      storageUsed: '0 GB',
    })
    dashboardService.getRecentActivity.mockResolvedValue([])
    dashboardService.getStatistics.mockResolvedValue({})
    
    renderWithProviders(<Dashboard />)
    
    await waitFor(() => {
      expect(screen.getByText('0')).toBeInTheDocument()
      expect(screen.getByText(/활동이 없습니다/i)).toBeInTheDocument()
    })
  })
})
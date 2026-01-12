import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import { BrowserRouter } from 'react-router-dom'
import { AuthProvider } from '../../common/context/AuthContext'
import Sidebar from '../../components/layout/Sidebar'
import * as router from 'react-router-dom'

const renderWithProviders = (component) => {
  return render(
    <BrowserRouter>
      <AuthProvider>
        {component}
      </AuthProvider>
    </BrowserRouter>
  )
}

describe('Sidebar 컴포넌트', () => {
  it('사이드바가 올바르게 렌더링된다', () => {
    renderWithProviders(<Sidebar />)
    const sidebar = screen.getByRole('navigation')
    expect(sidebar).toBeInTheDocument()
  })

  it('네비게이션 메뉴가 올바르게 표시된다', () => {
    renderWithProviders(<Sidebar />)
    
    expect(screen.getByText('대시보드')).toBeInTheDocument()
    expect(screen.getByText('모델 관리')).toBeInTheDocument()
    expect(screen.getByText('팀 관리')).toBeInTheDocument()
  })

  it('메뉴 항목 클릭 시 해당 페이지로 이동한다', async () => {
    const user = userEvent.setup()
    const mockNavigate = vi.fn()
    vi.spyOn(router, 'useNavigate').mockReturnValue(mockNavigate)
    
    renderWithProviders(<Sidebar />)
    
    const dashboardLink = screen.getByText('대시보드')
    await user.click(dashboardLink)
    
    expect(mockNavigate).toHaveBeenCalledWith('/')
  })

  it('활성 메뉴가 올바르게 강조된다', () => {
    renderWithProviders(<Sidebar currentPath="/" />)
    
    const dashboardLink = screen.getByText('대시보드')
    expect(dashboardLink).toHaveClass('bg-blue-600')
  })

  it('로그아웃 버튼이 작동한다', async () => {
    const user = userEvent.setup()
    const handleLogout = vi.fn()
    
    renderWithProviders(<Sidebar onLogout={handleLogout} />)
    
    const logoutButton = screen.getByRole('button', { name: '로그아웃' })
    await user.click(logoutButton)
    
    expect(handleLogout).toHaveBeenCalledTimes(1)
  })

  it('사이드바가 접혀질 수 있다', async () => {
    const user = userEvent.setup()
    const { container } = renderWithProviders(<Sidebar />)
    
    const collapseButton = screen.getByRole('button', { name: /사이드바 접기/i })
    await user.click(collapseButton)
    
    const sidebar = container.querySelector('.sidebar')
    expect(sidebar).toHaveClass('w-20')
  })

  it('로고가 올바르게 표시된다', () => {
    renderWithProviders(<Sidebar />)
    const logo = screen.getByAltText('로고')
    expect(logo).toBeInTheDocument()
  })

  it('사용자 정보가 표시된다', () => {
    const user = { name: '홍길동', email: 'hong@example.com' }
    
    renderWithProviders(
      <AuthProvider value={{ user }}>
        <Sidebar />
      </AuthProvider>
    )
    
    expect(screen.getByText('홍길동')).toBeInTheDocument()
  })

  it('모바일 환경에서 햄버거 메뉴가 표시된다', () => {
    // 모바일 뷰포트 시뮬레이션
    window.innerWidth = 375
    renderWithProviders(<Sidebar />)
    
    const hamburgerButton = screen.getByRole('button', { name: /메뉴/i })
    expect(hamburgerButton).toBeInTheDocument()
  })

  it('서브메뉴가 올바르게 표시된다', async () => {
    const user = userEvent.setup()
    renderWithProviders(<Sidebar />)
    
    const menuButton = screen.getByText('팀 관리')
    await user.click(menuButton)
    
    const subMenu = screen.getByText('팀 목록')
    expect(subMenu).toBeInTheDocument()
  })
})
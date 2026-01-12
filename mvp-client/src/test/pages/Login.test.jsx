import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { BrowserRouter } from 'react-router-dom'
import { AuthProvider } from '../common/context/AuthContext'
import Login from '../../pages/Login'

vi.mock('../../common/services/authService', () => ({
  authService: {
    login: vi.fn(),
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

describe('Login 페이지', () => {
  beforeEach(() => {
    localStorage.clear()
  })

  it('로그인 폼이 올바르게 렌더링된다', () => {
    renderWithProviders(<Login />)
    
    expect(screen.getByText('로그인')).toBeInTheDocument()
    expect(screen.getByLabelText(/이메일/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/비밀번호/i)).toBeInTheDocument()
  })

  it('이메일과 비밀번호를 입력할 수 있다', async () => {
    const user = userEvent.setup()
    renderWithProviders(<Login />)
    
    const emailInput = screen.getByLabelText(/이메일/i)
    const passwordInput = screen.getByLabelText(/비밀번호/i)
    
    await user.type(emailInput, 'test@example.com')
    await user.type(passwordInput, 'password123')
    
    expect(emailInput).toHaveValue('test@example.com')
    expect(passwordInput).toHaveValue('password123')
  })

  it('유효한 자격증명으로 로그인 성공', async () => {
    const user = userEvent.setup()
    const { authService } = await import('../../common/services/authService')
    authService.login.mockResolvedValue({
      token: 'fake-token',
      user: { id: 1, email: 'test@example.com', name: '테스트 사용자' },
    })
    
    renderWithProviders(<Login />)
    
    const emailInput = screen.getByLabelText(/이메일/i)
    const passwordInput = screen.getByLabelText(/비밀번호/i)
    const loginButton = screen.getByRole('button', { name: '로그인' })
    
    await user.type(emailInput, 'test@example.com')
    await user.type(passwordInput, 'password123')
    await user.click(loginButton)
    
    await waitFor(() => {
      expect(localStorage.getItem('token')).toBe('fake-token')
    })
  })

  it('로그인 실패 시 에러 메시지가 표시된다', async () => {
    const user = userEvent.setup()
    const { authService } = await import('../../common/services/authService')
    authService.login.mockRejectedValue(new Error('이메일 또는 비밀번호가 올바르지 않습니다.'))
    
    renderWithProviders(<Login />)
    
    const emailInput = screen.getByLabelText(/이메일/i)
    const passwordInput = screen.getByLabelText(/비밀번호/i)
    const loginButton = screen.getByRole('button', { name: '로그인' })
    
    await user.type(emailInput, 'wrong@example.com')
    await user.type(passwordInput, 'wrongpassword')
    await user.click(loginButton)
    
    await waitFor(() => {
      const errorMessage = screen.getByText(/이메일 또는 비밀번호가 올바르지 않습니다/i)
      expect(errorMessage).toBeInTheDocument()
    })
  })

  it('입력되지 않은 상태에서 로그인 버튼이 비활성화된다', () => {
    renderWithProviders(<Login />)
    
    const loginButton = screen.getByRole('button', { name: '로그인' })
    expect(loginButton).toBeDisabled()
  })

  it('비밀번호 필드가 보이기/숨기기가 가능하다', async () => {
    const user = userEvent.setup()
    renderWithProviders(<Login />)
    
    const passwordInput = screen.getByLabelText(/비밀번호/i)
    expect(passwordInput).toHaveAttribute('type', 'password')
    
    const toggleButton = screen.getByRole('button', { name: /보기/i })
    await user.click(toggleButton)
    
    expect(passwordInput).toHaveAttribute('type', 'text')
  })

  it('폼 유효성 검사가 작동한다', async () => {
    const user = userEvent.setup()
    renderWithProviders(<Login />)
    
    const emailInput = screen.getByLabelText(/이메일/i)
    await user.type(emailInput, 'invalid-email')
    await user.tab()
    
    await waitFor(() => {
      const errorMessage = screen.getByText(/올바른 이메일 형식을 입력해주세요/i)
      expect(errorMessage).toBeInTheDocument()
    })
  })

  it('로딩 중에는 로그인 버튼이 비활성화된다', async () => {
    const user = userEvent.setup()
    const { authService } = await import('../../common/services/authService')
    authService.login.mockImplementation(() => new Promise(() => {}))
    
    renderWithProviders(<Login />)
    
    const emailInput = screen.getByLabelText(/이메일/i)
    const passwordInput = screen.getByLabelText(/비밀번호/i)
    const loginButton = screen.getByRole('button', { name: '로그인' })
    
    await user.type(emailInput, 'test@example.com')
    await user.type(passwordInput, 'password123')
    await user.click(loginButton)
    
    await waitFor(() => {
      expect(loginButton).toBeDisabled()
      expect(screen.getByText(/로그인 중/i)).toBeInTheDocument()
    })
  })
})
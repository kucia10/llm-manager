import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { authService } from '../../common/services/authService'
import apiClient from '../../common/utils/apiClient'

vi.mock('../../common/utils/apiClient', () => ({
  default: {
    post: vi.fn(),
    get: vi.fn(),
  },
}))

describe('authService', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()
  })

  afterEach(() => {
    localStorage.clear()
  })

  describe('login', () => {
    it('올바른 자격증명으로 로그인 성공', async () => {
      const mockResponse = {
        token: 'fake-token',
        user: { id: 1, email: 'test@example.com', name: '테스트 사용자' },
      }
      apiClient.post.mockResolvedValue(mockResponse)

      const credentials = {
        email: 'test@example.com',
        password: 'password123',
      }

      const result = await authService.login(credentials)

      expect(apiClient.post).toHaveBeenCalledWith('/api/auth/login', credentials)
      expect(result).toEqual(mockResponse)
    })

    it('잘못된 자격증명으로 로그인 실패', async () => {
      const mockError = new Error('이메일 또는 비밀번호가 올바르지 않습니다.')
      apiClient.post.mockRejectedValue(mockError)

      const credentials = {
        email: 'wrong@example.com',
        password: 'wrongpassword',
      }

      await expect(authService.login(credentials)).rejects.toThrow('이메일 또는 비밀번호가 올바르지 않습니다.')
      expect(apiClient.post).toHaveBeenCalledWith('/api/auth/login', credentials)
    })

    it('네트워크 에러 발생 시 적절히 처리', async () => {
      const mockError = new Error('네트워크 연결 오류')
      apiClient.post.mockRejectedValue(mockError)

      const credentials = {
        email: 'test@example.com',
        password: 'password123',
      }

      await expect(authService.login(credentials)).rejects.toThrow('네트워크 연결 오류')
      expect(apiClient.post).toHaveBeenCalledWith('/api/auth/login', credentials)
    })

    it('빈 자격증명으로 로그인 시도', async () => {
      const mockError = new Error('이메일과 비밀번호를 입력해주세요.')
      apiClient.post.mockRejectedValue(mockError)

      const credentials = {
        email: '',
        password: '',
      }

      await expect(authService.login(credentials)).rejects.toThrow('이메일과 비밀번호를 입력해주세요.')
      expect(apiClient.post).toHaveBeenCalledWith('/api/auth/login', credentials)
    })
  })

  describe('getMe', () => {
    it('현재 사용자 정보 조회 성공', async () => {
      const mockResponse = {
        id: 1,
        email: 'test@example.com',
        name: '테스트 사용자',
        role: 'user',
      }
      apiClient.get.mockResolvedValue(mockResponse)

      const result = await authService.getMe()

      expect(apiClient.get).toHaveBeenCalledWith('/api/auth/me')
      expect(result).toEqual(mockResponse)
    })

    it('인증되지 않은 사용자 정보 조회 실패', async () => {
      const mockError = new Error('인증이 필요합니다.')
      apiClient.get.mockRejectedValue(mockError)

      await expect(authService.getMe()).rejects.toThrow('인증이 필요합니다.')
      expect(apiClient.get).toHaveBeenCalledWith('/api/auth/me')
    })

    it('토큰 만료로 인한 조회 실패', async () => {
      const mockError = new Error('인증이 만료되었습니다. 다시 로그인해주세요.')
      apiClient.get.mockRejectedValue(mockError)

      await expect(authService.getMe()).rejects.toThrow('인증이 만료되었습니다. 다시 로그인해주세요.')
      expect(apiClient.get).toHaveBeenCalledWith('/api/auth/me')
    })
  })

  describe('API Client Integration', () => {
    it('login은 apiClient의 post 메서드를 사용한다', async () => {
      const mockResponse = { token: 'test-token' }
      apiClient.post.mockResolvedValue(mockResponse)

      await authService.login({ email: 'test@test.com', password: '123' })

      expect(apiClient.post).toHaveBeenCalled()
    })

    it('getMe는 apiClient의 get 메서드를 사용한다', async () => {
      const mockResponse = { id: 1, email: 'test@test.com' }
      apiClient.get.mockResolvedValue(mockResponse)

      await authService.getMe()

      expect(apiClient.get).toHaveBeenCalled()
    })

    it('요청 시 올바른 엔드포인트로 호출된다', async () => {
      apiClient.post.mockResolvedValue({ token: 'test-token' })
      apiClient.get.mockResolvedValue({ id: 1, email: 'test@test.com' })

      await authService.login({ email: 'test@test.com', password: '123' })
      await authService.getMe()

      expect(apiClient.post).toHaveBeenCalledWith('/api/auth/login', expect.any(Object))
      expect(apiClient.get).toHaveBeenCalledWith('/api/auth/me')
    })
  })
})
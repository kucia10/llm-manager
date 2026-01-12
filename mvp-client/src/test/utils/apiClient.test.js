import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import apiClient from '../../common/utils/apiClient'

describe('apiClient', () => {
  let originalFetch
  let originalLocalStorage

  beforeEach(() => {
    originalFetch = global.fetch
    originalLocalStorage = global.localStorage

    global.fetch = vi.fn()

    localStorage.clear()
    localStorage.setItem = vi.fn()
    localStorage.getItem = vi.fn((key) => {
      if (key === 'token') return 'test-token'
      return null
    })
    localStorage.removeItem = vi.fn()

    vi.spyOn(console, 'error').mockImplementation(() => {})
  })

  afterEach(() => {
    global.fetch = originalFetch
    global.localStorage = originalLocalStorage
    vi.restoreAllMocks()
  })

  describe('constructor', () => {
    it('기본 baseURL이 설정된다', () => {
      expect(apiClient.baseURL).toBe(import.meta.env.VITE_API_BASE_URL || 'http://localhost:3000')
    })

    it('기본 헤더가 설정된다', () => {
      expect(apiClient.defaultHeaders).toEqual({
        'Content-Type': 'application/json',
      })
    })
  })

  describe('getAuthHeader', () => {
    it('토큰이 있으면 Authorization 헤더를 반환한다', () => {
      localStorage.getItem.mockReturnValue('test-token')

      const headers = apiClient.getAuthHeader()

      expect(headers).toEqual({
        Authorization: 'Bearer test-token',
      })
    })

    it('토큰이 없으면 빈 객체를 반환한다', () => {
      localStorage.getItem.mockReturnValue(null)

      const headers = apiClient.getAuthHeader()

      expect(headers).toEqual({})
    })
  })

  describe('GET requests', () => {
    it('GET 요청이 올바르게 전송된다', async () => {
      const mockResponse = { data: 'test' }
      global.fetch.mockResolvedValue({
        ok: true,
        json: async () => mockResponse,
      })

      const result = await apiClient.get('/api/test')

      expect(global.fetch).toHaveBeenCalledWith(
        expect.stringContaining('/api/test'),
        expect.objectContaining({
          method: 'GET',
          headers: expect.objectContaining({
            'Content-Type': 'application/json',
            Authorization: 'Bearer test-token',
          }),
        })
      )
      expect(result).toEqual(mockResponse)
    })

    it('GET 요청 시 추가 옵션을 사용할 수 있다', async () => {
      const mockResponse = { data: 'test' }
      global.fetch.mockResolvedValue({
        ok: true,
        json: async () => mockResponse,
      })

      const customHeaders = { 'X-Custom-Header': 'custom-value' }
      await apiClient.get('/api/test', { headers: customHeaders })

      expect(global.fetch).toHaveBeenCalledWith(
        expect.stringContaining('/api/test'),
        expect.objectContaining({
          method: 'GET',
          headers: expect.objectContaining({
            'X-Custom-Header': 'custom-value',
          }),
        })
      )
    })
  })

  describe('POST requests', () => {
    it('POST 요청이 올바르게 전송된다', async () => {
      const mockResponse = { id: 1, name: 'test' }
      const requestData = { name: 'test', value: 123 }
      global.fetch.mockResolvedValue({
        ok: true,
        json: async () => mockResponse,
      })

      const result = await apiClient.post('/api/test', requestData)

      expect(global.fetch).toHaveBeenCalledWith(
        expect.stringContaining('/api/test'),
        expect.objectContaining({
          method: 'POST',
          body: JSON.stringify(requestData),
          headers: expect.objectContaining({
            'Content-Type': 'application/json',
            Authorization: 'Bearer test-token',
          }),
        })
      )
      expect(result).toEqual(mockResponse)
    })
  })

  describe('PUT requests', () => {
    it('PUT 요청이 올바르게 전송된다', async () => {
      const mockResponse = { id: 1, name: 'updated' }
      const requestData = { name: 'updated' }
      global.fetch.mockResolvedValue({
        ok: true,
        json: async () => mockResponse,
      })

      const result = await apiClient.put('/api/test/1', requestData)

      expect(global.fetch).toHaveBeenCalledWith(
        expect.stringContaining('/api/test/1'),
        expect.objectContaining({
          method: 'PUT',
          body: JSON.stringify(requestData),
          headers: expect.objectContaining({
            'Content-Type': 'application/json',
            Authorization: 'Bearer test-token',
          }),
        })
      )
      expect(result).toEqual(mockResponse)
    })
  })

  describe('DELETE requests', () => {
    it('DELETE 요청이 올바르게 전송된다', async () => {
      const mockResponse = { success: true }
      global.fetch.mockResolvedValue({
        ok: true,
        json: async () => mockResponse,
      })

      const result = await apiClient.delete('/api/test/1')

      expect(global.fetch).toHaveBeenCalledWith(
        expect.stringContaining('/api/test/1'),
        expect.objectContaining({
          method: 'DELETE',
          headers: expect.objectContaining({
            'Content-Type': 'application/json',
            Authorization: 'Bearer test-token',
          }),
        })
      )
      expect(result).toEqual(mockResponse)
    })
  })

  describe('Error handling', () => {
    it('401 에러 발생 시 토큰을 제거하고 로그아웃 처리한다', async () => {
      global.fetch.mockResolvedValue({
        status: 401,
        ok: false,
        json: async () => ({ message: 'Unauthorized' }),
      })

      await expect(apiClient.get('/api/protected')).rejects.toThrow('인증이 만료되었습니다. 다시 로그인해주세요.')

      expect(localStorage.removeItem).toHaveBeenCalledWith('token')
    })

    it('401 에러 발생 시 로그인 페이지로 리다이렉트한다', async () => {
      global.fetch.mockResolvedValue({
        status: 401,
        ok: false,
        json: async () => ({ message: 'Unauthorized' }),
      })

      const originalLocation = window.location
      delete window.location
      window.location = { href: '' }

      await expect(apiClient.get('/api/protected')).rejects.toThrow()

      expect(window.location.href).toBe('/login')

      window.location = originalLocation
    })

    it('일반적인 HTTP 에러를 처리한다', async () => {
      global.fetch.mockResolvedValue({
        status: 404,
        ok: false,
        json: async () => ({ message: 'Not Found' }),
      })

      await expect(apiClient.get('/api/notfound')).rejects.toThrow('Not Found')
    })

    it('에러 메시지가 없으면 기본 에러 메시지를 반환한다', async () => {
      global.fetch.mockResolvedValue({
        status: 500,
        ok: false,
        json: async () => {
          throw new Error('JSON parse error')
        },
      })

      await expect(apiClient.get('/api/error')).rejects.toThrow()
    })

    it('네트워크 에러를 처리한다', async () => {
      global.fetch.mockRejectedValue(new Error('Network Error'))

      await expect(apiClient.get('/api/test')).rejects.toThrow('Network Error')
      expect(console.error).toHaveBeenCalledWith('API Request Error:', expect.any(Error))
    })
  })

  describe('URL construction', () => {
    it('baseURL과 endpoint를 올바르게 결합한다', async () => {
      global.fetch.mockResolvedValue({
        ok: true,
        json: async () => ({}),
      })

      await apiClient.get('/api/test')

      expect(global.fetch).toHaveBeenCalledWith(
        expect.stringContaining('/api/test'),
        expect.any(Object)
      )
    })
  })
})
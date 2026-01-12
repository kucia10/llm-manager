import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import { BrowserRouter } from 'react-router-dom'
import { AuthProvider } from '../../common/context/AuthContext'
import Layout from '../../components/layout/Layout'

const renderWithProviders = (component) => {
  return render(
    <BrowserRouter>
      <AuthProvider>
        {component}
      </AuthProvider>
    </BrowserRouter>
  )
}

describe('Layout 컴포넌트', () => {
  it('래이아웃이 올바르게 렌더링된다', () => {
    renderWithProviders(
      <Layout>
        <div>테스트 콘텐츠</div>
      </Layout>
    )
    
    expect(screen.getByText('테스트 콘텐츠')).toBeInTheDocument()
  })

  it('사이드바가 렌더링된다', () => {
    renderWithProviders(
      <Layout>
        <div>내용</div>
      </Layout>
    )
    
    const sidebar = screen.getByRole('navigation')
    expect(sidebar).toBeInTheDocument()
  })

  it('헤더가 렌더링된다', () => {
    renderWithProviders(
      <Layout>
        <div>내용</div>
      </Layout>
    )
    
    const header = screen.getByRole('banner')
    expect(header).toBeInTheDocument()
  })

  it('메인 콘텐츠 영역이 올바르게 표시된다', () => {
    renderWithProviders(
      <Layout>
        <div>메인 내용</div>
      </Layout>
    )
    
    const mainContent = screen.getByText('메인 내용')
    expect(mainContent).toBeInTheDocument()
  })

  it('반응형 레이아웃이 적용된다', () => {
    const { container } = renderWithProviders(
      <Layout>
        <div>반응형 테스트</div>
      </Layout>
    )
    
    const layout = container.querySelector('.layout')
    expect(layout).toHaveClass('flex')
  })

  it('테마가 올바르게 적용된다', () => {
    const { container } = renderWithProviders(
      <Layout>
        <div>테마 테스트</div>
      </Layout>
    )
    
    const layout = container.querySelector('.layout')
    expect(layout).toHaveClass('bg-gray-50')
  })
})
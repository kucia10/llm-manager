import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import Button from '../../../components/common/Button'

describe('Button 컴포넌트', () => {
  it('버튼이 올바르게 렌더링된다', () => {
    render(<Button>테스트 버튼</Button>)
    const button = screen.getByRole('button', { name: '테스트 버튼' })
    expect(button).toBeInTheDocument()
  })

  it('onClick 핸들러가 호출된다', async () => {
    const user = userEvent.setup()
    const handleClick = vi.fn()
    render(<Button onClick={handleClick}>클릭</Button>)
    
    const button = screen.getByRole('button', { name: '클릭' })
    await user.click(button)
    
    expect(handleClick).toHaveBeenCalledTimes(1)
  })

  it('disabled 상태일 때 클릭되지 않는다', async () => {
    const user = userEvent.setup()
    const handleClick = vi.fn()
    render(<Button disabled onClick={handleClick}>비활성</Button>)
    
    const button = screen.getByRole('button', { name: '비활성' })
    await user.click(button)
    
    expect(handleClick).not.toHaveBeenCalled()
  })

  it('다양한 variant가 올바르게 적용된다', () => {
    const { rerender } = render(<Button variant="primary">Primary</Button>)
    expect(screen.getByRole('button')).toHaveClass('bg-blue-600')

    rerender(<Button variant="secondary">Secondary</Button>)
    expect(screen.getByRole('button')).toHaveClass('bg-gray-600')
  })

  it('로딩 상태일 때 텍스트가 변경된다', () => {
    render(<Button loading>제출</Button>)
    const button = screen.getByRole('button')
    expect(button).toHaveTextContent(/로딩 중/i)
    expect(button).toBeDisabled()
  })

  it('아이콘이 올바르게 표시된다', () => {
    render(<Button icon={<span>🔍</span>}>검색</Button>)
    const button = screen.getByRole('button')
    expect(button).toContainHTML('<span>🔍</span>')
  })

  it('전체 너비(full) 옵션이 적용된다', () => {
    render(<Button fullWidth>전체 버튼</Button>)
    const button = screen.getByRole('button')
    expect(button).toHaveClass('w-full')
  })
})
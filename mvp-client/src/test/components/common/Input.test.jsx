import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import Input from '../../../components/common/Input'

describe('Input 컴포넌트', () => {
  it('인풋이 올바르게 렌더링된다', () => {
    render(<Input placeholder="이름을 입력하세요" />)
    const input = screen.getByPlaceholderText('이름을 입력하세요')
    expect(input).toBeInTheDocument()
  })

  it('값이 변경되면 onChange 핸들러가 호출된다', async () => {
    const user = userEvent.setup()
    const handleChange = vi.fn()
    render(<Input onChange={handleChange} />)
    
    const input = screen.getByRole('textbox')
    await user.type(input, '테스트')
    
    expect(handleChange).toHaveBeenCalled()
  })

  it('label이 올바르게 표시된다', () => {
    render(<Input label="이메일" />)
    const label = screen.getByText('이메일')
    expect(label).toBeInTheDocument()
  })

  it('disabled 상태일 때 입력되지 않는다', async () => {
    const user = userEvent.setup()
    render(<Input disabled />)
    
    const input = screen.getByRole('textbox')
    await user.type(input, '테스트')
    
    expect(input).not.toHaveValue()
  })

  it('에러 상태일 때 에러 메시지가 표시된다', () => {
    render(<Input error="필수 입력 항목입니다" />)
    const errorMessage = screen.getByText('필수 입력 항목입니다')
    expect(errorMessage).toBeInTheDocument()
  })

  it('type 속성이 올바르게 적용된다', () => {
    render(<Input type="email" />)
    const input = screen.getByRole('textbox')
    expect(input).toHaveAttribute('type', 'email')
  })

  it('필수 필드 표시가 올바르게 작동한다', () => {
    render(<Input label="이름" required />)
    const input = screen.getByLabelText('이름 *')
    expect(input).toHaveAttribute('required')
  })
})
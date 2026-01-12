import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import Card from '../../../components/common/Card'

describe('Card 컴포넌트', () => {
  it('카드가 올바르게 렌더링된다', () => {
    render(<Card title="테스트 카드">카드 내용</Card>)
    const cardTitle = screen.getByText('테스트 카드')
    expect(cardTitle).toBeInTheDocument()
  })

  it('자식 콘텐츠가 올바르게 표시된다', () => {
    render(
      <Card title="제목">
        <p>카드 본문 내용</p>
      </Card>
    )
    const content = screen.getByText('카드 본문 내용')
    expect(content).toBeInTheDocument()
  })

  it('onClick 핸들러가 호출된다', async () => {
    const user = userEvent.setup()
    const handleClick = vi.fn()
    
    render(
      <Card title="클릭 가능한 카드" onClick={handleClick}>
        클릭하세요
      </Card>
    )
    
    const card = screen.getByText('클릭하세요').closest('.cursor-pointer')
    await user.click(card)
    
    expect(handleClick).toHaveBeenCalledTimes(1)
  })

  it('다양한 variant가 올바르게 적용된다', () => {
    const { rerender } = render(<Card variant="default" title="기본">내용</Card>)
    expect(screen.getByRole('article')).toHaveClass('bg-white')

    rerender(<Card variant="primary" title="프라이머리">내용</Card>)
    expect(screen.getByRole('article')).toHaveClass('bg-blue-600')

    rerender(<Card variant="danger" title="데인져">내용</Card>)
    expect(screen.getByRole('article')).toHaveClass('bg-red-600')
  })

  it('아이콘이 올바르게 표시된다', () => {
    render(
      <Card title="아이콘 카드" icon={<span>🔔</span>}>
        알림 내용
      </Card>
    )
    const icon = screen.getByText('🔔')
    expect(icon).toBeInTheDocument()
  })

  it('hover 상태일 때 스타일이 변경된다', () => {
    render(
      <Card title="호버 카드" hoverable>
        호버 효과 테스트
      </Card>
    )
    const card = screen.getByRole('article')
    expect(card).toHaveClass('hover:shadow-lg')
  })

  it('footer 내용이 올바르게 표시된다', () => {
    render(
      <Card 
        title="푸터 카드" 
        footer={<button>자세히 보기</button>}
      >
        본문 내용
      </Card>
    )
    const footerButton = screen.getByRole('button', { name: '자세히 보기' })
    expect(footerButton).toBeInTheDocument()
  })
})
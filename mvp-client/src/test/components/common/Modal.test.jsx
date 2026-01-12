import { describe, it, expect, vi } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import Modal from '../../../components/common/Modal'

describe('Modal 컴포넌트', () => {
  it('open 상태일 때 모달이 렌더링된다', () => {
    render(<Modal isOpen={true} title="테스트 모달">내용</Modal>)
    const modalTitle = screen.getByText('테스트 모달')
    expect(modalTitle).toBeInTheDocument()
  })

  it('close 상태일 때 모달이 렌더링되지 않는다', () => {
    render(<Modal isOpen={false} title="닫힌 모달">내용</Modal>)
    const modalTitle = screen.queryByText('닫힌 모달')
    expect(modalTitle).not.toBeInTheDocument()
  })

  it('닫기 버튼이 작동한다', async () => {
    const user = userEvent.setup()
    const onClose = vi.fn()
    
    render(
      <Modal isOpen={true} title="닫기 테스트" onClose={onClose}>
        모달 내용
      </Modal>
    )
    
    const closeButton = screen.getByRole('button', { name: /닫기/i })
    await user.click(closeButton)
    
    expect(onClose).toHaveBeenCalledTimes(1)
  })

  it('백그라운드 클릭으로 닫힌다', async () => {
    const user = userEvent.setup()
    const onClose = vi.fn()
    
    render(
      <Modal isOpen={true} title="백그라운드 클릭" onClose={onClose}>
        내용
      </Modal>
    )
    
    const backdrop = screen.getByTestId('modal-backdrop')
    await user.click(backdrop)
    
    expect(onClose).toHaveBeenCalledTimes(1)
  })

  it('확인 버튼이 작동한다', async () => {
    const user = userEvent.setup()
    const onConfirm = vi.fn()
    
    render(
      <Modal isOpen={true} title="확인 테스트" onConfirm={onConfirm}>
        확인하시겠습니까?
      </Modal>
    )
    
    const confirmButton = screen.getByRole('button', { name: '확인' })
    await user.click(confirmButton)
    
    expect(onConfirm).toHaveBeenCalledTimes(1)
  })

  it('취소 버튼이 작동한다', async () => {
    const user = userEvent.setup()
    const onCancel = vi.fn()
    
    render(
      <Modal isOpen={true} title="취소 테스트" onCancel={onCancel}>
        작업을 취소하시겠습니까?
      </Modal>
    )
    
    const cancelButton = screen.getByRole('button', { name: '취소' })
    await user.click(cancelButton)
    
    expect(onCancel).toHaveBeenCalledTimes(1)
  })

  it('ESC 키로 닫힌다', async () => {
    const user = userEvent.setup()
    const onClose = vi.fn()
    
    render(
      <Modal isOpen={true} title="ESC 테스트" onClose={onClose}>
        ESC를 누르세요
      </Modal>
    )
    
    await user.keyboard('{Escape}')
    
    expect(onClose).toHaveBeenCalledTimes(1)
  })

  it('size 속성이 올바르게 적용된다', () => {
    const { rerender } = render(<Modal isOpen={true} title="사이즈 테스트" size="small">내용</Modal>)
    expect(screen.getByRole('dialog')).toHaveClass('max-w-md')

    rerender(<Modal isOpen={true} title="사이즈 테스트" size="large">내용</Modal>)
    expect(screen.getByRole('dialog')).toHaveClass('max-w-4xl')
  })

  it('사용자 정의 푸터가 표시된다', () => {
    render(
      <Modal 
        isOpen={true} 
        title="푸터 테스트" 
        footer={<button>사용자 정의 버튼</button>}
      >
        내용
      </Modal>
    )
    
    const customButton = screen.getByRole('button', { name: '사용자 정의 버튼' })
    expect(customButton).toBeInTheDocument()
  })
})
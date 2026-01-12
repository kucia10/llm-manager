import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import Table from '../../../components/common/Table'

describe('Table 컴포넌트', () => {
  const mockColumns = [
    { key: 'id', label: 'ID' },
    { key: 'name', label: '이름' },
    { key: 'email', label: '이메일' },
  ]

  const mockData = [
    { id: 1, name: '홍길동', email: 'hong@example.com' },
    { id: 2, name: '김철수', email: 'kim@example.com' },
  ]

  it('테이블이 올바르게 렌더링된다', () => {
    render(<Table columns={mockColumns} data={mockData} />)
    
    expect(screen.getByText('ID')).toBeInTheDocument()
    expect(screen.getByText('이름')).toBeInTheDocument()
    expect(screen.getByText('이메일')).toBeInTheDocument()
  })

  it('데이터가 올바르게 표시된다', () => {
    render(<Table columns={mockColumns} data={mockData} />)
    
    expect(screen.getByText('홍길동')).toBeInTheDocument()
    expect(screen.getByText('김철수')).toBeInTheDocument()
    expect(screen.getByText('hong@example.com')).toBeInTheDocument()
  })

  it('빈 데이터일 때 메시지가 표시된다', () => {
    render(<Table columns={mockColumns} data={[]} />)
    const emptyMessage = screen.getByText(/데이터가 없습니다/i)
    expect(emptyMessage).toBeInTheDocument()
  })

  it('로딩 상태일 때 스피너가 표시된다', () => {
    render(<Table columns={mockColumns} data={mockData} loading />)
    const spinner = screen.getByRole('status')
    expect(spinner).toBeInTheDocument()
  })

  it('선택 가능한 행을 클릭할 수 있다', async () => {
    const user = userEvent.setup()
    const handleRowClick = vi.fn()
    
    render(
      <Table 
        columns={mockColumns} 
        data={mockData} 
        onRowClick={handleRowClick}
      />
    )
    
    const firstRow = screen.getByText('홍길동').closest('tr')
    await user.click(firstRow)
    
    expect(handleRowClick).toHaveBeenCalledWith(mockData[0])
  })

  it('페이지네이션이 작동한다', async () => {
    const user = userEvent.setup()
    const onPageChange = vi.fn()
    
    render(
      <Table 
        columns={mockColumns} 
        data={mockData} 
        pagination={{
          current: 1,
          total: 100,
          pageSize: 10,
          onChange: onPageChange,
        }}
      />
    )
    
    const nextPageButton = screen.getByRole('button', { name: '다음' })
    await user.click(nextPageButton)
    
    expect(onPageChange).toHaveBeenCalledWith(2)
  })

  it('정렬이 작동한다', async () => {
    const user = userEvent.setup()
    const onSort = vi.fn()
    
    render(
      <Table 
        columns={mockColumns} 
        data={mockData} 
        sortable
        onSort={onSort}
      />
    )
    
    const nameHeader = screen.getByText('이름')
    await user.click(nameHeader)
    
    expect(onSort).toHaveBeenCalledWith('name')
  })

  it('셀 렌더링이 올바르게 작동한다', () => {
    const customColumns = [
      {
        key: 'status',
        label: '상태',
        render: (value) => (
          <span className={value === '활성' ? 'text-green-600' : 'text-red-600'}>
            {value}
          </span>
        ),
      },
    ]
    
    const data = [{ status: '활성' }]
    render(<Table columns={customColumns} data={data} />)
    
    const statusCell = screen.getByText('활성')
    expect(statusCell).toHaveClass('text-green-600')
  })

  it('검색이 작동한다', async () => {
    const user = userEvent.setup()
    const onSearch = vi.fn()
    
    render(
      <Table 
        columns={mockColumns} 
        data={mockData} 
        searchable
        onSearch={onSearch}
      />
    )
    
    const searchInput = screen.getByPlaceholderText(/검색/i)
    await user.type(searchInput, '홍길동')
    
    expect(onSearch).toHaveBeenCalledWith('홍길동')
  })
})
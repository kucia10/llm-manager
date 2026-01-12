import { useNavigate } from 'react-router-dom';

const Home = () => {
  const navigate = useNavigate();
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100">
      <div className="container mx-auto px-4 py-16">
        <div className="text-center">
          <h1 className="text-5xl font-bold text-gray-900 mb-4">
            Welcome to MVP Client
          </h1>
          <p className="text-xl text-gray-600 mb-8">
            React + Vite + Tailwind CSS로 구축된 최신 프론트엔드 아키텍처
          </p>
          <div className="flex justify-center gap-4">
            <button
              onClick={() => navigate('/login')}
              className="px-6 py-3 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors"
            >
              시작하기
            </button>
            <button className="px-6 py-3 bg-white text-gray-700 rounded-lg hover:bg-gray-50 transition-colors border border-gray-300">
              문서 보기
            </button>
          </div>
        </div>
        
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mt-16">
          <div className="bg-white p-6 rounded-lg shadow-md">
            <h3 className="text-xl font-semibold text-gray-800 mb-3">⚡ 빠른 개발</h3>
            <p className="text-gray-600">Vite의 HMR(Hot Module Replacement)으로 빠른 개발 경험 제공</p>
          </div>
          <div className="bg-white p-6 rounded-lg shadow-md">
            <h3 className="text-xl font-semibold text-gray-800 mb-3">🎨 Tailwind CSS</h3>
            <p className="text-gray-600">유틸리티 기반의 CSS 프레임워크로 빠르고 일관된 스타일링</p>
          </div>
          <div className="bg-white p-6 rounded-lg shadow-md">
            <h3 className="text-xl font-semibold text-gray-800 mb-3">📦 모듈형 구조</h3>
            <p className="text-gray-600">확장 가능하고 유지보수가 쉬운 컴포넌트 기반 아키텍처</p>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Home
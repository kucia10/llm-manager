import React, { useEffect, useState } from 'react';
import { useAuth } from '../common/context/AuthContext';
import { dashboardService } from '../common/services/dashboardService';
import { teamService } from '../common/services/teamService';
import { modelService } from '../common/services/modelService';
import { PieChart, Pie, Cell, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import Card from '../components/common/Card';
import Loading from '../components/common/Loading';
import Modal from '../components/common/Modal';

const Dashboard = () => {
  const { user } = useAuth();
  const [dashboardData, setDashboardData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedModal, setSelectedModal] = useState(null);
  const [modalData, setModalData] = useState(null);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      const data = await dashboardService.getDashboardData();
      setDashboardData(data);
    } catch (error) {
      console.error('대시보드 데이터 로드 실패:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCardClick = async (type) => {
    try {
      if (type === 'teams') {
        const teams = await teamService.getAllTeams();
        setModalData({ type: '팀 목록', items: teams });
      } else if (type === 'models') {
        const models = await modelService.getAllModels();
        setModalData({ type: '모델 목록', items: models });
      }
      setSelectedModal(type);
    } catch (error) {
      console.error('데이터 로드 실패:', error);
    }
  };

  const closeModal = () => {
    setSelectedModal(null);
    setModalData(null);
  };

  if (loading) {
    return <Loading />;
  }

  return (
    <div className="max-w-7xl mx-auto">
      <div className="mb-8">
        <h1 className="text-3xl font-bold mb-2">대시보드</h1>
        <p className="text-gray-600">환영합니다, {user?.username || '사용자'}님</p>
      </div>
      
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <Card>
          <div className="text-sm text-gray-600 mb-2">총 사용량</div>
          <div className="text-2xl font-bold">
            {dashboardData?.totalUsage?.toLocaleString() || 0} 토큰
          </div>
        </Card>
        
        <Card>
          <div className="text-sm text-gray-600 mb-2">총 비용</div>
          <div className="text-2xl font-bold">
            ${dashboardData?.totalCost?.toFixed(2) || 0}
          </div>
        </Card>
        
        <Card
          className="cursor-pointer hover:shadow-lg transition-shadow"
          onClick={() => handleCardClick('teams')}
        >
          <div className="text-sm text-gray-600 mb-2 flex justify-between items-center">
            <span>팀 수</span>
            <span className="text-xs text-blue-500">클릭하여 상세보기</span>
          </div>
          <div className="text-2xl font-bold">
            {dashboardData?.totalTeams || 0}
          </div>
        </Card>
        
        <Card
          className="cursor-pointer hover:shadow-lg transition-shadow"
          onClick={() => handleCardClick('models')}
        >
          <div className="text-sm text-gray-600 mb-2 flex justify-between items-center">
            <span>모델 수</span>
            <span className="text-xs text-blue-500">클릭하여 상세보기</span>
          </div>
          <div className="text-2xl font-bold">
            {dashboardData?.totalModels || 0}
          </div>
        </Card>
      </div>
      
      {/* 차트 영역 */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card title="팀별 사용량">
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={dashboardData?.teamUsageSummaries?.map(team => ({
                  name: team.teamName,
                  value: team.usage
                })) || []}
                cx="50%"
                cy="50%"
                labelLine={false}
                label={(entry) => `${entry.name}: ${(entry.value / 1000).toFixed(0)}k`}
                outerRadius={80}
                fill="#8884d8"
              >
                {dashboardData?.teamUsageSummaries?.map((_, index) => (
                  <Cell key={`cell-${index}`} fill={`hsl(${index * 60}, 70%, 50%)`} />
                ))}
              </Pie>
              <Tooltip formatter={(value) => `${value?.toLocaleString()} 토큰`} />
            </PieChart>
          </ResponsiveContainer>
        </Card>
        
        <Card title="팀별 할당량 대비 사용률">
          <ResponsiveContainer width="100%" height={300}>
            <BarChart
              data={dashboardData?.teamUsageSummaries?.map(team => ({
                name: team.teamName,
                사용률: team.usagePercentage || 0,
                할당량: team.quota,
                사용량: team.usage
              })) || []}
              margin={{ top: 20, right: 30, left: 20, bottom: 5 }}
            >
              <CartesianGrid strokeDasharray="3 3" vertical={false} />
              <XAxis dataKey="name" />
              <YAxis tickFormatter={(value) => `${value}%`} />
              <Tooltip
                formatter={(value, name) => {
                  if (name === '사용률') return [`${value?.toFixed(1)}%`, name];
                  return [`${value?.toLocaleString()} 토큰`, name];
                }}
              />
              <Legend />
              <Bar dataKey="사용률" fill="#3b82f6" name="사용률" />
            </BarChart>
          </ResponsiveContainer>
        </Card>
      </div>

      {/* 모달 */}
      <Modal
        isOpen={!!selectedModal}
        onClose={closeModal}
        title={modalData?.type || ''}
        size="xlarge"
      >
        {modalData?.items && modalData.items.length > 0 ? (
          <div className="space-y-4">
            {modalData.type === '팀 목록' && (
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">팀 이름</th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">할당량</th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">사용량</th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">사용률</th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {modalData.items.map((team) => (
                      <tr key={team.id} className="hover:bg-gray-50">
                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{team.name}</td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{team.quota?.toLocaleString()} 토큰</td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{team.usage?.toLocaleString()} 토큰</td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                          {team.quota ? ((team.usage / team.quota) * 100).toFixed(1) : 0}%
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}

            {modalData.type === '모델 목록' && (
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">모델 이름</th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">제공사</th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">비용(토큰당)</th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">상태</th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {modalData.items.map((model) => (
                      <tr key={model.id} className="hover:bg-gray-50">
                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{model.name}</td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{model.provider}</td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${model.costPerToken?.toFixed(6)}</td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm">
                          <span className={`px-2 py-1 rounded-full text-xs font-medium ${model.isActive ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
                            {model.isActive ? '활성' : '비활성'}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        ) : (
          <div className="text-center py-8 text-gray-500">
            데이터가 없습니다.
          </div>
        )}
      </Modal>
    </div>
  );
};

export default Dashboard;
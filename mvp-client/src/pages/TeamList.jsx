import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../common/context/AuthContext';
import { teamService } from '../common/services/teamService';
import Table from '../components/common/Table';
import Button from '../components/Button';
import Card from '../components/common/Card';
import Loading from '../components/common/Loading';
import Modal from '../components/common/Modal';

const TeamList = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [teams, setTeams] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedTeam, setSelectedTeam] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editMode, setEditMode] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    quota: 0
  });

  useEffect(() => {
    loadTeams();
  }, []);

  const loadTeams = async () => {
    try {
      const data = await teamService.getAllTeams();
      setTeams(data);
    } catch (error) {
      console.error('팀 목록 로드 실패:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleRowClick = async (team) => {
    try {
      const teamDetail = await teamService.getTeamById(team.id);
      setSelectedTeam(teamDetail);
      setFormData({
        name: teamDetail.name,
        quota: teamDetail.quota
      });
      setEditMode(true);
      setIsModalOpen(true);
    } catch (error) {
      console.error('팀 상세 조회 실패:', error);
    }
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedTeam(null);
    setEditMode(false);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'quota' ? Number(value) : value
    }));
  };

  const handleUpdate = async () => {
    console.log('팀 수정 시작:', formData);
    try {
      const result = await teamService.updateTeam(selectedTeam.id, formData);
      console.log('팀 수정 결과:', result);
      await loadTeams();
      setSelectedTeam(prev => ({
        ...prev,
        ...formData
      }));
      alert('팀이 성공적으로 수정되었습니다.');
    } catch (error) {
      console.error('팀 수정 실패:', error);
      alert(`팀 수정에 실패했습니다: ${error.message || error}`);
    }
  };

  const handleDelete = async () => {
    if (!window.confirm(`"${selectedTeam.name}" 팀을 삭제하시겠습니까?`)) {
      return;
    }

    try {
      await teamService.deleteTeam(selectedTeam.id);
      await loadTeams();
      handleCloseModal();
    } catch (error) {
      console.error('팀 삭제 실패:', error);
      alert('팀 삭제에 실패했습니다.');
    }
  };

  const columns = [
    { header: 'ID', key: 'id' },
    { header: '팀 이름', key: 'name' },
    { 
      header: '할당량', 
      key: 'quota',
      render: (value) => `${value?.toLocaleString() || 0} 토큰`
    },
    { 
      header: '사용량', 
      key: 'usage',
      render: (value) => `${value?.toLocaleString() || 0} 토큰`
    },
    {
      header: '사용률',
      key: 'usageRate',
      render: (value, row) => {
        const rate = row.quota > 0 ? ((row.usage / row.quota) * 100).toFixed(1) : 0;
        const color = rate > 80 ? 'text-red-600' : 'text-green-600';
        return <span className={color}>{rate}%</span>;
      }
    }
  ];

  if (loading) {
    return <Loading />;
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <Card 
        title="팀 목록"
        actions={
          <Button onClick={() => navigate('/teams/create')}>
            팀 생성
          </Button>
        }
      >
        <Table 
          columns={columns} 
          data={teams}
          onRowClick={handleRowClick}
        />
      </Card>

      {/* 팀 상세/수정 모달 */}
      <Modal
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        title={editMode ? '팀 수정' : '팀 상세 정보'}
        size="medium"
      >
        {selectedTeam && (
          <div className="space-y-4">
            {/* ID */}
            <div className="border-b pb-3">
              <label className="block text-sm font-medium text-gray-700 mb-1">ID</label>
              <p className="text-gray-900">{selectedTeam.id}</p>
            </div>

            {/* 팀 이름 */}
            <div className="border-b pb-3">
              <label className="block text-sm font-medium text-gray-700 mb-1">팀 이름</label>
              {editMode ? (
                <input
                  type="text"
                  name="name"
                  value={formData.name}
                  onChange={handleInputChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              ) : (
                <p className="text-gray-900">{selectedTeam.name}</p>
              )}
            </div>

            {/* 할당량 */}
            <div className="border-b pb-3">
              <label className="block text-sm font-medium text-gray-700 mb-1">할당량 (토큰)</label>
              {editMode ? (
                <input
                  type="number"
                  name="quota"
                  value={formData.quota}
                  onChange={handleInputChange}
                  min="0"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              ) : (
                <p className="text-gray-900">{selectedTeam.quota.toLocaleString()} 토큰</p>
              )}
            </div>

            {/* 사용량 */}
            <div className="border-b pb-3">
              <label className="block text-sm font-medium text-gray-700 mb-1">사용량 (토큰)</label>
              <p className="text-gray-900">{selectedTeam.usage.toLocaleString()} 토큰</p>
            </div>

            {/* 사용률 */}
            <div className="border-b pb-3">
              <label className="block text-sm font-medium text-gray-700 mb-1">사용률</label>
              <p className="text-gray-900">
                {selectedTeam.quota > 0 
                  ? ((selectedTeam.usage / selectedTeam.quota) * 100).toFixed(1)
                  : 0}%
              </p>
            </div>

            {/* 생성일/수정일 */}
            <div className="border-b pb-3">
              <label className="block text-sm font-medium text-gray-700 mb-1">생성일</label>
              <p className="text-gray-900 text-sm">
                {new Date(selectedTeam.createdAt).toLocaleString('ko-KR')}
              </p>
            </div>

            <div className="pb-3">
              <label className="block text-sm font-medium text-gray-700 mb-1">수정일</label>
              <p className="text-gray-900 text-sm">
                {new Date(selectedTeam.updatedAt).toLocaleString('ko-KR')}
              </p>
            </div>

            {/* 버튼 그룹 */}
            <div className="flex gap-3 pt-4">
              <>
                <Button onClick={handleUpdate} className="flex-1">
                  저장
                </Button>
                <Button
                  onClick={handleDelete}
                  variant="danger"
                  className="flex-1"
                >
                  삭제
                </Button>
              </>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default TeamList;
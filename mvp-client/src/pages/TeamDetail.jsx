import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../common/context/AuthContext';
import { teamService } from '../common/services/teamService';
import Button from '../components/Button';
import Card from '../components/common/Card';
import Loading from '../components/common/Loading';

const TeamDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [team, setTeam] = useState(null);
  const [members, setMembers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showEditModal, setShowEditModal] = useState(false);

  useEffect(() => {
    loadTeamData();
  }, [id]);

  const loadTeamData = async () => {
    try {
      const [teamData, membersData] = await Promise.all([
        teamService.getTeamById(id),
        teamService.getTeamMembers(id)
      ]);
      setTeam(teamData);
      setMembers(membersData);
    } catch (error) {
      console.error('팀 정보 로드 실패:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (window.confirm('정말 이 팀을 삭제하시겠습니까?')) {
      try {
        await teamService.deleteTeam(id);
        navigate('/teams');
      } catch (error) {
        console.error('팀 삭제 실패:', error);
        alert('팀 삭제에 실패했습니다.');
      }
    }
  };

  if (loading) {
    return <Loading />;
  }

  if (!team) {
    return (
      <div className="container mx-auto px-4 py-8">
        <Card>
          <p className="text-center text-gray-500">팀을 찾을 수 없습니다.</p>
          <Button 
            onClick={() => navigate('/teams')}
            className="mt-4"
          >
            목록으로 돌아가기
          </Button>
        </Card>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="mb-6 flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold mb-2">{team.name}</h1>
          <p className="text-gray-600">팀 ID: {team.id}</p>
        </div>
        <div className="flex gap-2">
          <Button 
            variant="secondary"
            onClick={() => setShowEditModal(true)}
          >
            수정
          </Button>
          <Button 
            variant="danger"
            onClick={handleDelete}
          >
            삭제
          </Button>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
        <Card title="팀 정보">
          <div className="space-y-2">
            <div>
              <span className="text-sm text-gray-600">팀 이름:</span>
              <span className="ml-2 font-medium">{team.name}</span>
            </div>
            <div>
              <span className="text-sm text-gray-600">할당량:</span>
              <span className="ml-2 font-medium">{team.quota?.toLocaleString() || 0} 토큰</span>
            </div>
            <div>
              <span className="text-sm text-gray-600">사용량:</span>
              <span className="ml-2 font-medium">{team.usage?.toLocaleString() || 0} 토큰</span>
            </div>
            <div>
              <span className="text-sm text-gray-600">사용률:</span>
              <span className="ml-2 font-medium">
                {team.quota > 0 ? ((team.usage / team.quota) * 100).toFixed(1) : 0}%
              </span>
            </div>
          </div>
        </Card>

        <Card title="구성원">
          {members.length === 0 ? (
            <p className="text-gray-500">구성원이 없습니다.</p>
          ) : (
            <ul className="space-y-2">
              {members.map(member => (
                <li key={member.id} className="text-sm">
                  <span className="font-medium">{member.username}</span>
                  {member.role && (
                    <span className="ml-2 text-gray-600">({member.role})</span>
                  )}
                </li>
              ))}
            </ul>
          )}
        </Card>
      </div>
    </div>
  );
};

export default TeamDetail;
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../common/context/AuthContext';
import { teamService } from '../common/services/teamService';
import Card from '../components/common/Card';
import Button from '../components/Button';
import Loading from '../components/common/Loading';

const TeamCreate = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    quota: 0
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'quota' ? parseInt(value) || 0 : value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      await teamService.createTeam(formData);
      navigate('/teams');
    } catch (error) {
      console.error('팀 생성 실패:', error);
      alert('팀 생성에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    navigate('/teams');
  };

  return (
    <div className="container mx-auto px-4 py-8">
      <Card title="팀 생성">
        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label htmlFor="name" className="block text-sm font-medium text-gray-700 mb-2">
              팀 이름
            </label>
            <input
              type="text"
              id="name"
              name="name"
              value={formData.name}
              onChange={handleChange}
              required
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="팀 이름을 입력하세요"
            />
          </div>

          <div>
            <label htmlFor="quota" className="block text-sm font-medium text-gray-700 mb-2">
              할당량 (토큰)
            </label>
            <input
              type="number"
              id="quota"
              name="quota"
              value={formData.quota}
              onChange={handleChange}
              required
              min="0"
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="할당량을 입력하세요"
            />
          </div>

          <div className="flex gap-4 pt-4">
            <Button
              type="button"
              variant="secondary"
              onClick={handleCancel}
              disabled={loading}
            >
              취소
            </Button>
            <Button
              type="submit"
              disabled={loading}
            >
              {loading ? <Loading /> : '생성'}
            </Button>
          </div>
        </form>
      </Card>
    </div>
  );
};

export default TeamCreate;
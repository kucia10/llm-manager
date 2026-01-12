import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../common/context/AuthContext';
import { modelService } from '../common/services/modelService';
import Card from '../components/common/Card';
import Button from '../components/Button';
import Loading from '../components/common/Loading';

const ModelCreate = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    provider: '',
    costPerToken: 0,
    apiKey: ''
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'costPerToken' ? parseFloat(value) || 0 : value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      await modelService.createModel(formData);
      navigate('/models');
    } catch (error) {
      console.error('모델 생성 실패:', error);
      alert('모델 생성에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    navigate('/models');
  };

  return (
    <div className="container mx-auto px-4 py-8">
      <Card title="모델 생성">
        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label htmlFor="name" className="block text-sm font-medium text-gray-700 mb-2">
              모델 이름
            </label>
            <input
              type="text"
              id="name"
              name="name"
              value={formData.name}
              onChange={handleChange}
              required
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="모델 이름을 입력하세요"
            />
          </div>

          <div>
            <label htmlFor="provider" className="block text-sm font-medium text-gray-700 mb-2">
              제공자
            </label>
            <select
              id="provider"
              name="provider"
              value={formData.provider}
              onChange={handleChange}
              required
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              <option value="">제공자를 선택하세요</option>
              <option value="OpenAI">OpenAI</option>
              <option value="Anthropic">Anthropic</option>
              <option value="Google">Google</option>
              <option value="Meta">Meta</option>
              <option value="Other">기타</option>
            </select>
          </div>

          <div>
            <label htmlFor="costPerToken" className="block text-sm font-medium text-gray-700 mb-2">
              토큰당 비용 ($)
            </label>
            <input
              type="number"
              id="costPerToken"
              name="costPerToken"
              value={formData.costPerToken}
              onChange={handleChange}
              required
              min="0"
              step="0.000001"
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="토큰당 비용을 입력하세요"
            />
          </div>

          <div>
            <label htmlFor="apiKey" className="block text-sm font-medium text-gray-700 mb-2">
              API Key (선택)
            </label>
            <input
              type="text"
              id="apiKey"
              name="apiKey"
              value={formData.apiKey}
              onChange={handleChange}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="API Key를 입력하세요"
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

export default ModelCreate;
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../common/context/AuthContext';
import { modelService } from '../common/services/modelService';
import Table from '../components/common/Table';
import Button from '../components/Button';
import Card from '../components/common/Card';
import Loading from '../components/common/Loading';
import Modal from '../components/common/Modal';

const ModelList = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [models, setModels] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedModel, setSelectedModel] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    provider: '',
    apiKey: '',
    costPerToken: 0,
    isActive: true
  });

  useEffect(() => {
    loadModels();
  }, []);

  const loadModels = async () => {
    try {
      const data = await modelService.getAllModels();
      setModels(data);
    } catch (error) {
      console.error('모델 목록 로드 실패:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleRowClick = async (model) => {
    try {
      const modelDetail = await modelService.getModelById(model.id);
      setSelectedModel(modelDetail);
      setFormData({
        name: modelDetail.name,
        provider: modelDetail.provider,
        apiKey: modelDetail.apiKey,
        costPerToken: modelDetail.costPerToken,
        isActive: modelDetail.isActive
      });
      setIsModalOpen(true);
    } catch (error) {
      console.error('모델 상세 조회 실패:', error);
    }
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedModel(null);
  };

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : (name === 'costPerToken' ? Number(value) : value)
    }));
  };

  const handleUpdate = async () => {
    console.log('모델 수정 시작:', formData);
    try {
      const result = await modelService.updateModel(selectedModel.id, formData);
      console.log('모델 수정 결과:', result);
      await loadModels();
      setSelectedModel(prev => ({
        ...prev,
        ...formData
      }));
      alert('모델이 성공적으로 수정되었습니다.');
    } catch (error) {
      console.error('모델 수정 실패:', error);
      alert(`모델 수정에 실패했습니다: ${error.message || error}`);
    }
  };

  const handleDelete = async () => {
    if (!window.confirm(`"${selectedModel.name}" 모델을 삭제하시겠습니까?`)) {
      return;
    }

    try {
      await modelService.deleteModel(selectedModel.id);
      await loadModels();
      handleCloseModal();
    } catch (error) {
      console.error('모델 삭제 실패:', error);
      alert('모델 삭제에 실패했습니다.');
    }
  };

  const columns = [
    { header: 'ID', key: 'id' },
    { header: '모델 이름', key: 'name' },
    { header: '제공자', key: 'provider' },
    { 
      header: '토큰당 비용', 
      key: 'costPerToken',
      render: (value) => `$${value?.toFixed(6) || 0}`
    },
    {
      header: '상태',
      key: 'isActive',
      render: (value) => (
        <span className={`px-2 py-1 rounded-full text-xs font-medium ${
          value ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
        }`}>
          {value ? '활성' : '비활성'}
        </span>
      )
    }
  ];

  if (loading) {
    return <Loading />;
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <Card 
        title="모델 목록"
        actions={
          <Button onClick={() => navigate('/models/create')}>
            모델 생성
          </Button>
        }
      >
        <Table 
          columns={columns} 
          data={models}
          onRowClick={handleRowClick}
        />
      </Card>

      {/* 모델 편집 모달 */}
      <Modal
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        title="모델 편집"
        size="medium"
      >
        {selectedModel && (
          <div className="space-y-4">
            {/* ID */}
            <div className="border-b pb-3">
              <label className="block text-sm font-medium text-gray-700 mb-1">ID</label>
              <p className="text-gray-900">{selectedModel.id}</p>
            </div>

            {/* 모델 이름 */}
            <div className="border-b pb-3">
              <label className="block text-sm font-medium text-gray-700 mb-1">모델 이름</label>
              <input
                type="text"
                name="name"
                value={formData.name}
                onChange={handleInputChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            {/* 제공자 */}
            <div className="border-b pb-3">
              <label className="block text-sm font-medium text-gray-700 mb-1">제공자</label>
              <input
                type="text"
                name="provider"
                value={formData.provider}
                onChange={handleInputChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            {/* API Key */}
            <div className="border-b pb-3">
              <label className="block text-sm font-medium text-gray-700 mb-1">API Key</label>
              <input
                type="password"
                name="apiKey"
                value={formData.apiKey}
                onChange={handleInputChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            {/* 토큰당 비용 */}
            <div className="border-b pb-3">
              <label className="block text-sm font-medium text-gray-700 mb-1">토큰당 비용 ($)</label>
              <input
                type="number"
                name="costPerToken"
                value={formData.costPerToken}
                onChange={handleInputChange}
                min="0"
                step="0.000001"
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            {/* 활성 상태 */}
            <div className="border-b pb-3">
              <label className="flex items-center">
                <input
                  type="checkbox"
                  name="isActive"
                  checked={formData.isActive}
                  onChange={handleInputChange}
                  className="mr-2 h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                />
                <span className="text-sm font-medium text-gray-700">활성 상태</span>
              </label>
            </div>

            {/* 생성일/수정일 */}
            <div className="border-b pb-3">
              <label className="block text-sm font-medium text-gray-700 mb-1">생성일</label>
              <p className="text-gray-900 text-sm">
                {new Date(selectedModel.createdAt).toLocaleString('ko-KR')}
              </p>
            </div>

            <div className="pb-3">
              <label className="block text-sm font-medium text-gray-700 mb-1">수정일</label>
              <p className="text-gray-900 text-sm">
                {new Date(selectedModel.updatedAt).toLocaleString('ko-KR')}
              </p>
            </div>

            {/* 버튼 그룹 */}
            <div className="flex gap-3 pt-4">
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
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default ModelList;
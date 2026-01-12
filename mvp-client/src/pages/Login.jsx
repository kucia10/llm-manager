import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../common/context/AuthContext';
import Button from '../components/Button';
import Input from '../components/common/Input';
import Card from '../components/common/Card';

const Login = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [formData, setFormData] = useState({ username: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    
    try {
      await login(formData);
      navigate('/dashboard');
    } catch (err) {
      setError('로그인에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (name, value) => {
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <Card title="로그인" className="w-full max-w-md">
        <form onSubmit={handleSubmit} className="space-y-4">
          <Input
            label="사용자명"
            type="text"
            value={formData.username}
            onChange={handleChange}
            name="username"
            required
            placeholder="사용자명을 입력하세요"
          />
          <Input
            label="비밀번호"
            type="password"
            value={formData.password}
            onChange={handleChange}
            name="password"
            required
            placeholder="비밀번호를 입력하세요"
          />
          {error && <p className="text-red-600 text-sm">{error}</p>}
          <Button 
            type="submit" 
            className="w-full"
            disabled={loading}
          >
            {loading ? '로그인 중...' : '로그인'}
          </Button>
        </form>
      </Card>
    </div>
  );
};

export default Login;
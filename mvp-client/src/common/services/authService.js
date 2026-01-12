import apiClient from '../utils/apiClient';

export const authService = {
  login: async (credentials) => {
    return apiClient.post('/api/auth/login', credentials);
  },

  getMe: async () => {
    return apiClient.get('/api/auth/me');
  }
};
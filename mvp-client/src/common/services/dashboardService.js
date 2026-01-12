import apiClient from '../utils/apiClient';

export const dashboardService = {
  getDashboardData: async () => {
    return apiClient.get('/api/dashboard');
  }
};
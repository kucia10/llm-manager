import apiClient from '../utils/apiClient';

export const modelService = {
  getAllModels: async () => {
    return apiClient.get('/api/models');
  },

  getModelById: async (id) => {
    return apiClient.get(`/api/models/${id}`);
  },

  createModel: async (data) => {
    return apiClient.post('/api/models', data);
  },

  updateModel: async (id, data) => {
    return apiClient.put(`/api/models/${id}`, data);
  },

  deleteModel: async (id) => {
    return apiClient.delete(`/api/models/${id}`);
  }
};
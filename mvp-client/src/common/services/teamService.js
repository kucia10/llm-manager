import apiClient from '../utils/apiClient';

export const teamService = {
  getAllTeams: async () => {
    return apiClient.get('/api/teams');
  },

  getTeamById: async (id) => {
    return apiClient.get(`/api/teams/${id}`);
  },

  createTeam: async (data) => {
    return apiClient.post('/api/teams', data);
  },

  updateTeam: async (id, data) => {
    return apiClient.put(`/api/teams/${id}`, data);
  },

  deleteTeam: async (id) => {
    return apiClient.delete(`/api/teams/${id}`);
  },

  getTeamMembers: async (teamId) => {
    return apiClient.get(`/api/teams/${teamId}/members`);
  }
};
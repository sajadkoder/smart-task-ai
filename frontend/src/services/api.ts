import axios from 'axios';
import type { AuthResponse, RegisterRequest, LoginRequest, Task, TaskRequest, PageResponse } from '../types';

const API_BASE_URL = '/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  register: (data: RegisterRequest) => 
    api.post<AuthResponse>('/auth/register', data),
  
  login: (data: LoginRequest) => 
    api.post<AuthResponse>('/auth/login', data),
};

// Task API
export const taskAPI = {
  getAll: (page = 0, size = 10, sortBy = 'createdAt', sortDir = 'desc') =>
    api.get<PageResponse<Task>>('/tasks', {
      params: { page, size, sortBy, sortDir },
    }),

  getById: (id: number) =>
    api.get<Task>(`/tasks/${id}`),

  create: (data: TaskRequest) =>
    api.post<Task>('/tasks', data),

  update: (id: number, data: TaskRequest) =>
    api.put<Task>(`/tasks/${id}`, data),

  delete: (id: number) =>
    api.delete(`/tasks/${id}`),

  getByStatus: (status: string) =>
    api.get<Task[]>(`/tasks/status/${status}`),

  getByCategory: (category: string) =>
    api.get<Task[]>(`/tasks/category/${category}`),

  getByPriority: (priority: string) =>
    api.get<Task[]>(`/tasks/priority/${priority}`),

  getOverdue: () =>
    api.get<Task[]>('/tasks/overdue'),

  getOrdered: () =>
    api.get<Task[]>('/tasks/ordered'),

  updateStatus: (id: number, status: string) =>
    api.patch<Task>(`/tasks/${id}/status`, null, { params: { status } }),

  updatePosition: (id: number, position: number) =>
    api.patch<Task>(`/tasks/${id}/position`, null, { params: { position } }),

  // AI endpoints
  summarize: () =>
    api.post<{ summary: string }>('/tasks/ai/summarize'),

  getSuggestion: (id: number) =>
    api.post<{ suggestion: string }>(`/tasks/${id}/ai/suggestion`),

  analyzeProductivity: () =>
    api.post<{ analysis: string }>('/tasks/ai/productivity'),
};

export default api;

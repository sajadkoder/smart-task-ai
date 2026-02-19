import { create } from 'zustand';
import { taskAPI } from '../services/api';
import type { Task, TaskRequest, TaskStatus, TaskCategory, TaskPriority } from '../types';

interface TaskState {
  tasks: Task[];
  isLoading: boolean;
  error: string | null;
  selectedTask: Task | null;
  
  fetchTasks: () => Promise<void>;
  fetchTasksByStatus: (status: TaskStatus) => Promise<void>;
  fetchTasksByCategory: (category: TaskCategory) => Promise<void>;
  fetchTasksByPriority: (priority: TaskPriority) => Promise<void>;
  fetchOverdueTasks: () => Promise<void>;
  createTask: (data: TaskRequest) => Promise<Task>;
  updateTask: (id: number, data: TaskRequest) => Promise<Task>;
  deleteTask: (id: number) => Promise<void>;
  updateTaskStatus: (id: number, status: TaskStatus) => Promise<void>;
  updateTaskPosition: (id: number, position: number) => Promise<void>;
  selectTask: (task: Task | null) => void;
  clearError: () => void;
}

export const useTaskStore = create<TaskState>((set, get) => ({
  tasks: [],
  isLoading: false,
  error: null,
  selectedTask: null,

  fetchTasks: async () => {
    set({ isLoading: true, error: null });
    try {
      const response = await taskAPI.getOrdered();
      set({ tasks: response.data, isLoading: false });
    } catch (error: unknown) {
      const message = error instanceof Error ? error.message : 'Failed to fetch tasks';
      set({ error: message, isLoading: false });
    }
  },

  fetchTasksByStatus: async (status: TaskStatus) => {
    set({ isLoading: true, error: null });
    try {
      const response = await taskAPI.getByStatus(status);
      set({ tasks: response.data, isLoading: false });
    } catch (error: unknown) {
      const message = error instanceof Error ? error.message : 'Failed to fetch tasks';
      set({ error: message, isLoading: false });
    }
  },

  fetchTasksByCategory: async (category: TaskCategory) => {
    set({ isLoading: true, error: null });
    try {
      const response = await taskAPI.getByCategory(category);
      set({ tasks: response.data, isLoading: false });
    } catch (error: unknown) {
      const message = error instanceof Error ? error.message : 'Failed to fetch tasks';
      set({ error: message, isLoading: false });
    }
  },

  fetchTasksByPriority: async (priority: TaskPriority) => {
    set({ isLoading: true, error: null });
    try {
      const response = await taskAPI.getByPriority(priority);
      set({ tasks: response.data, isLoading: false });
    } catch (error: unknown) {
      const message = error instanceof Error ? error.message : 'Failed to fetch tasks';
      set({ error: message, isLoading: false });
    }
  },

  fetchOverdueTasks: async () => {
    set({ isLoading: true, error: null });
    try {
      const response = await taskAPI.getOverdue();
      set({ tasks: response.data, isLoading: false });
    } catch (error: unknown) {
      const message = error instanceof Error ? error.message : 'Failed to fetch overdue tasks';
      set({ error: message, isLoading: false });
    }
  },

  createTask: async (data: TaskRequest) => {
    set({ isLoading: true, error: null });
    try {
      const response = await taskAPI.create(data);
      const currentTasks = get().tasks;
      set({ tasks: [...currentTasks, response.data], isLoading: false });
      return response.data;
    } catch (error: unknown) {
      const message = error instanceof Error ? error.message : 'Failed to create task';
      set({ error: message, isLoading: false });
      throw error;
    }
  },

  updateTask: async (id: number, data: TaskRequest) => {
    set({ isLoading: true, error: null });
    try {
      const response = await taskAPI.update(id, data);
      const currentTasks = get().tasks;
      const updatedTasks = currentTasks.map(task => 
        task.id === id ? response.data : task
      );
      set({ tasks: updatedTasks, isLoading: false });
      return response.data;
    } catch (error: unknown) {
      const message = error instanceof Error ? error.message : 'Failed to update task';
      set({ error: message, isLoading: false });
      throw error;
    }
  },

  deleteTask: async (id: number) => {
    set({ isLoading: true, error: null });
    try {
      await taskAPI.delete(id);
      const currentTasks = get().tasks;
      const filteredTasks = currentTasks.filter(task => task.id !== id);
      set({ tasks: filteredTasks, isLoading: false });
    } catch (error: unknown) {
      const message = error instanceof Error ? error.message : 'Failed to delete task';
      set({ error: message, isLoading: false });
      throw error;
    }
  },

  updateTaskStatus: async (id: number, status: TaskStatus) => {
    try {
      const response = await taskAPI.updateStatus(id, status);
      const currentTasks = get().tasks;
      const updatedTasks = currentTasks.map(task => 
        task.id === id ? response.data : task
      );
      set({ tasks: updatedTasks });
    } catch (error: unknown) {
      const message = error instanceof Error ? error.message : 'Failed to update task status';
      set({ error: message });
      throw error;
    }
  },

  updateTaskPosition: async (id: number, position: number) => {
    try {
      const response = await taskAPI.updatePosition(id, position);
      const currentTasks = get().tasks;
      const updatedTasks = currentTasks.map(task => 
        task.id === id ? response.data : task
      );
      set({ tasks: updatedTasks });
    } catch (error: unknown) {
      const message = error instanceof Error ? error.message : 'Failed to update task position';
      set({ error: message });
      throw error;
    }
  },

  selectTask: (task: Task | null) => set({ selectedTask: task }),

  clearError: () => set({ error: null }),
}));

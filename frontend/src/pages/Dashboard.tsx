import { useEffect, useState } from 'react';
import { DragDropContext, Droppable, Draggable, DropResult } from '@hello-pangea/dnd';
import { useTaskStore } from '../store/useTaskStore';
import { useAuthStore } from '../store/useAuthStore';
import { taskAPI } from '../services/api';
import type { Task, TaskStatus } from '../types';
import { Plus, LogOut, Sparkles, Loader2 } from 'lucide-react';

const STATUS_COLUMNS: { status: TaskStatus; label: string; color: string }[] = [
  { status: 'PENDING', label: 'To Do', color: 'bg-gray-100' },
  { status: 'IN_PROGRESS', label: 'In Progress', color: 'bg-blue-100' },
  { status: 'COMPLETED', label: 'Done', color: 'bg-green-100' },
];

export default function Dashboard() {
  const { tasks, fetchTasks, updateTaskStatus, isLoading } = useTaskStore();
  const { user, logout } = useAuthStore();
  const [showAddModal, setShowAddModal] = useState(false);
  const [newTaskTitle, setNewTaskTitle] = useState('');
  const [newTaskDescription, setNewTaskDescription] = useState('');
  const [aiSummary, setAiSummary] = useState<string | null>(null);
  const [productivityAnalysis, setProductivityAnalysis] = useState<string | null>(null);
  const [isLoadingAI, setIsLoadingAI] = useState(false);

  useEffect(() => {
    fetchTasks();
  }, [fetchTasks]);

  const handleDragEnd = (result: DropResult) => {
    if (!result.destination) return;

    // destination.droppableId is like "PENDING-drop", "IN_PROGRESS-drop", "COMPLETED-drop"
    const statusFromDrop = result.destination.droppableId.replace('-drop', '') as TaskStatus;
    
    // draggableId is like "PENDING-123", extract task ID from it
    const draggableParts = result.draggableId.split('-');
    const taskId = parseInt(draggableParts[draggableParts.length - 1]);
    
    if (statusFromDrop && taskId && !isNaN(taskId)) {
      updateTaskStatus(taskId, statusFromDrop);
    }
  };

  const handleAddTask = async () => {
    if (!newTaskTitle.trim()) return;
    
    try {
      await taskAPI.create({
        title: newTaskTitle,
        description: newTaskDescription,
      });
      setNewTaskTitle('');
      setNewTaskDescription('');
      setShowAddModal(false);
      fetchTasks();
    } catch (error) {
      console.error('Failed to create task:', error);
    }
  };

  const handleGetAISummary = async () => {
    setIsLoadingAI(true);
    try {
      const response = await taskAPI.summarize();
      setAiSummary(response.data.summary);
    } catch (error) {
      console.error('Failed to get AI summary:', error);
    } finally {
      setIsLoadingAI(false);
    }
  };

  const handleGetProductivity = async () => {
    setIsLoadingAI(true);
    try {
      const response = await taskAPI.analyzeProductivity();
      setProductivityAnalysis(response.data.analysis);
    } catch (error) {
      console.error('Failed to get productivity analysis:', error);
    } finally {
      setIsLoadingAI(false);
    }
  };

  const getTasksByStatus = (status: TaskStatus) => {
    return tasks.filter((task) => task.status === status);
  };

  const handleLogout = () => {
    logout();
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">
          <h1 className="text-2xl font-bold text-gray-900">SmartTask AI</h1>
          <div className="flex items-center gap-4">
            <span className="text-gray-600">Welcome, {user?.username}</span>
            <button
              onClick={handleLogout}
              className="flex items-center gap-2 px-4 py-2 text-sm text-gray-700 hover:text-gray-900"
            >
              <LogOut size={18} />
              Logout
            </button>
          </div>
        </div>
      </header>

      {/* AI Actions */}
      <div className="max-w-7xl mx-auto px-4 py-4">
        <div className="flex gap-4 mb-6">
          <button
            onClick={() => setShowAddModal(true)}
            className="flex items-center gap-2 px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700"
          >
            <Plus size={18} />
            Add Task
          </button>
          
          <button
            onClick={handleGetAISummary}
            disabled={isLoadingAI}
            className="flex items-center gap-2 px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 disabled:opacity-50"
          >
            {isLoadingAI ? <Loader2 size={18} className="animate-spin" /> : <Sparkles size={18} />}
            AI Summary
          </button>
          
          <button
            onClick={handleGetProductivity}
            disabled={isLoadingAI}
            className="flex items-center gap-2 px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 disabled:opacity-50"
          >
            {isLoadingAI ? <Loader2 size={18} className="animate-spin" /> : <Sparkles size={18} />}
            Productivity Analysis
          </button>
        </div>

        {/* AI Results */}
        {(aiSummary || productivityAnalysis) && (
          <div className="mb-6 p-4 bg-white rounded-lg shadow">
            {aiSummary && (
              <div className="mb-4">
                <h3 className="font-semibold text-purple-700 mb-2">AI Summary</h3>
                <p className="text-gray-700">{aiSummary}</p>
              </div>
            )}
            {productivityAnalysis && (
              <div>
                <h3 className="font-semibold text-indigo-700 mb-2">Productivity Analysis</h3>
                <p className="text-gray-700">{productivityAnalysis}</p>
              </div>
            )}
          </div>
        )}
      </div>

      {/* Kanban Board */}
      <div className="max-w-7xl mx-auto px-4 pb-8">
        {isLoading ? (
          <div className="flex justify-center py-12">
            <Loader2 size={48} className="animate-spin text-primary-600" />
          </div>
        ) : (
          <DragDropContext onDragEnd={handleDragEnd}>
            <div className="grid grid-cols-3 gap-6">
              {STATUS_COLUMNS.map((column) => (
                <div key={column.status} className={`${column.color} rounded-lg p-4`}>
                  <h2 className="font-semibold text-gray-800 mb-4">{column.label}</h2>
                  
                  <Droppable droppableId={`${column.status}-drop`}>
                    {(provided) => (
                      <div
                        {...provided.droppableProps}
                        ref={provided.innerRef}
                        className="space-y-3 min-h-[200px]"
                      >
                        {getTasksByStatus(column.status).map((task, index) => (
                          <Draggable
                            key={task.id}
                            draggableId={`${task.status}-${task.id}`}
                            index={index}
                          >
                            {(provided, snapshot) => (
                              <div
                                ref={provided.innerRef}
                                {...provided.draggableProps}
                                {...provided.dragHandleProps}
                                className={`bg-white p-4 rounded-lg shadow-sm border border-gray-200 ${
                                  snapshot.isDragging ? 'shadow-lg' : ''
                                }`}
                              >
                                <h3 className="font-medium text-gray-900">{task.title}</h3>
                                {task.description && (
                                  <p className="text-sm text-gray-600 mt-1 line-clamp-2">
                                    {task.description}
                                  </p>
                                )}
                                <div className="flex items-center gap-2 mt-3">
                                  <span
                                    className={`text-xs px-2 py-1 rounded ${
                                      task.priority === 'URGENT'
                                        ? 'bg-red-100 text-red-700'
                                        : task.priority === 'HIGH'
                                        ? 'bg-orange-100 text-orange-700'
                                        : task.priority === 'MEDIUM'
                                        ? 'bg-yellow-100 text-yellow-700'
                                        : 'bg-gray-100 text-gray-700'
                                    }`}
                                  >
                                    {task.priority}
                                  </span>
                                  <span className="text-xs px-2 py-1 rounded bg-blue-100 text-blue-700">
                                    {task.category}
                                  </span>
                                </div>
                              </div>
                            )}
                          </Draggable>
                        ))}
                        {provided.placeholder}
                      </div>
                    )}
                  </Droppable>
                </div>
              ))}
            </div>
          </DragDropContext>
        )}
      </div>

      {/* Add Task Modal */}
      {showAddModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-lg p-6 max-w-md w-full">
            <h2 className="text-xl font-semibold mb-4">Add New Task</h2>
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Title
                </label>
                <input
                  type="text"
                  value={newTaskTitle}
                  onChange={(e) => setNewTaskTitle(e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md"
                  placeholder="Enter task title"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Description
                </label>
                <textarea
                  value={newTaskDescription}
                  onChange={(e) => setNewTaskDescription(e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md"
                  rows={3}
                  placeholder="Enter task description (optional)"
                />
              </div>
            </div>
            <div className="flex justify-end gap-3 mt-6">
              <button
                onClick={() => setShowAddModal(false)}
                className="px-4 py-2 text-gray-700 hover:text-gray-900"
              >
                Cancel
              </button>
              <button
                onClick={handleAddTask}
                className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700"
              >
                Add Task
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

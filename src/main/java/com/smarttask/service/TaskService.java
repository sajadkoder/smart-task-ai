package com.smarttask.service;

import com.smarttask.dto.TaskRequest;
import com.smarttask.dto.TaskResponse;
import com.smarttask.entity.Task;
import com.smarttask.entity.User;
import com.smarttask.repository.TaskRepository;
import com.smarttask.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Transactional
    public TaskResponse createTask(Long userId, TaskRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : Task.TaskStatus.PENDING)
                .priority(request.getPriority() != null ? request.getPriority() : Task.TaskPriority.MEDIUM)
                .category(request.getCategory() != null ? request.getCategory() : Task.TaskCategory.GENERAL)
                .dueDate(request.getDueDate())
                .position(request.getPosition() != null ? request.getPosition() : 0)
                .user(user)
                .build();

        Task savedTask = taskRepository.save(task);
        return TaskResponse.fromEntity(savedTask);
    }

    @Transactional
    public TaskResponse updateTask(Long userId, Long taskId, TaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this task");
        }

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
            if (request.getStatus() == Task.TaskStatus.COMPLETED) {
                task.setCompletedAt(LocalDateTime.now());
            }
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getCategory() != null) {
            task.setCategory(request.getCategory());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        if (request.getPosition() != null) {
            task.setPosition(request.getPosition());
        }

        Task updatedTask = taskRepository.save(task);
        return TaskResponse.fromEntity(updatedTask);
    }

    @Transactional
    public void deleteTask(Long userId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this task");
        }

        taskRepository.delete(task);
    }

    public TaskResponse getTaskById(Long userId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to access this task");
        }

        return TaskResponse.fromEntity(task);
    }

    public Page<TaskResponse> getAllTasks(Long userId, Pageable pageable) {
        Page<Task> tasks = taskRepository.findByUserId(userId, pageable);
        return tasks.map(TaskResponse::fromEntity);
    }

    public List<TaskResponse> getTasksByStatus(Long userId, Task.TaskStatus status) {
        List<Task> tasks = taskRepository.findByUserIdAndStatus(userId, status);
        return tasks.stream().map(TaskResponse::fromEntity).collect(Collectors.toList());
    }

    public List<TaskResponse> getTasksByCategory(Long userId, Task.TaskCategory category) {
        List<Task> tasks = taskRepository.findByUserIdAndCategory(userId, category);
        return tasks.stream().map(TaskResponse::fromEntity).collect(Collectors.toList());
    }

    public List<TaskResponse> getTasksByPriority(Long userId, Task.TaskPriority priority) {
        List<Task> tasks = taskRepository.findByUserIdAndPriority(userId, priority);
        return tasks.stream().map(TaskResponse::fromEntity).collect(Collectors.toList());
    }

    public List<TaskResponse> getOverdueTasks(Long userId) {
        List<Task> tasks = taskRepository.findOverdueTasks(userId, LocalDateTime.now());
        return tasks.stream().map(TaskResponse::fromEntity).collect(Collectors.toList());
    }

    public List<TaskResponse> getAllTasksOrdered(Long userId) {
        List<Task> tasks = taskRepository.findByUserIdOrderByPositionAsc(userId);
        return tasks.stream().map(TaskResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public TaskResponse updateTaskStatus(Long userId, Long taskId, Task.TaskStatus status) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this task");
        }

        task.setStatus(status);
        if (status == Task.TaskStatus.COMPLETED) {
            task.setCompletedAt(LocalDateTime.now());
        } else {
            task.setCompletedAt(null);
        }

        Task updatedTask = taskRepository.save(task);
        return TaskResponse.fromEntity(updatedTask);
    }

    @Transactional
    public TaskResponse updateTaskPosition(Long userId, Long taskId, Integer position) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this task");
        }

        task.setPosition(position);
        Task updatedTask = taskRepository.save(task);
        return TaskResponse.fromEntity(updatedTask);
    }
}

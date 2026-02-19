package com.smarttask.controller;

import com.smarttask.dto.TaskRequest;
import com.smarttask.dto.TaskResponse;
import com.smarttask.entity.Task;
import com.smarttask.repository.TaskRepository;
import com.smarttask.repository.UserRepository;
import com.smarttask.service.AIService;
import com.smarttask.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final AIService aiService;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TaskRequest request) {
        
        Long userId = getUserId(userDetails);
        
        if (request.getCategory() == null) {
            String suggestedCategory = aiService.categorizeTask(request.getTitle(), request.getDescription());
            try {
                Task.TaskCategory category = Task.TaskCategory.valueOf(suggestedCategory);
                request.setCategory(category);
            } catch (IllegalArgumentException e) {
                request.setCategory(Task.TaskCategory.GENERAL);
            }
        }
        
        if (request.getPriority() == null) {
            String suggestedPriority = aiService.suggestPriority(
                request.getTitle(), 
                request.getDescription(), 
                request.getCategory() != null ? request.getCategory().name() : "GENERAL"
            );
            try {
                Task.TaskPriority priority = Task.TaskPriority.valueOf(suggestedPriority);
                request.setPriority(priority);
            } catch (IllegalArgumentException e) {
                request.setPriority(Task.TaskPriority.MEDIUM);
            }
        }
        
        TaskResponse response = taskService.createTask(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getAllTasks(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Long userId = getUserId(userDetails);
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageRequest pageable = PageRequest.of(page, size, sort);
        
        Page<TaskResponse> tasks = taskService.getAllTasks(userId, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long taskId) {
        
        Long userId = getUserId(userDetails);
        TaskResponse task = taskService.getTaskById(userId, taskId);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long taskId,
            @Valid @RequestBody TaskRequest request) {
        
        Long userId = getUserId(userDetails);
        TaskResponse task = taskService.updateTask(userId, taskId, request);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long taskId) {
        
        Long userId = getUserId(userDetails);
        taskService.deleteTask(userId, taskId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskResponse>> getTasksByStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Task.TaskStatus status) {
        
        Long userId = getUserId(userDetails);
        List<TaskResponse> tasks = taskService.getTasksByStatus(userId, status);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<TaskResponse>> getTasksByCategory(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Task.TaskCategory category) {
        
        Long userId = getUserId(userDetails);
        List<TaskResponse> tasks = taskService.getTasksByCategory(userId, category);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<TaskResponse>> getTasksByPriority(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Task.TaskPriority priority) {
        
        Long userId = getUserId(userDetails);
        List<TaskResponse> tasks = taskService.getTasksByPriority(userId, priority);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<TaskResponse>> getOverdueTasks(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = getUserId(userDetails);
        List<TaskResponse> tasks = taskService.getOverdueTasks(userId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/ordered")
    public ResponseEntity<List<TaskResponse>> getAllTasksOrdered(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = getUserId(userDetails);
        List<TaskResponse> tasks = taskService.getAllTasksOrdered(userId);
        return ResponseEntity.ok(tasks);
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long taskId,
            @RequestParam Task.TaskStatus status) {
        
        Long userId = getUserId(userDetails);
        TaskResponse task = taskService.updateTaskStatus(userId, taskId, status);
        return ResponseEntity.ok(task);
    }

    @PatchMapping("/{taskId}/position")
    public ResponseEntity<TaskResponse> updateTaskPosition(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long taskId,
            @RequestParam Integer position) {
        
        Long userId = getUserId(userDetails);
        TaskResponse task = taskService.updateTaskPosition(userId, taskId, position);
        return ResponseEntity.ok(task);
    }

    @PostMapping("/ai/summarize")
    public ResponseEntity<Map<String, String>> summarizeTasks(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = getUserId(userDetails);
        List<Task> tasks = taskRepository.findByUserIdOrderByPositionAsc(userId);
        String summary = aiService.generateTaskSummary(tasks);
        return ResponseEntity.ok(Map.of("summary", summary));
    }

    @PostMapping("/{taskId}/ai/suggestion")
    public ResponseEntity<Map<String, String>> getTaskSuggestion(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long taskId) {
        
        Long userId = getUserId(userDetails);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        if (!task.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        String suggestion = aiService.generateTaskSuggestion(task);
        return ResponseEntity.ok(Map.of("suggestion", suggestion));
    }

    @PostMapping("/ai/productivity")
    public ResponseEntity<Map<String, String>> analyzeProductivity(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = getUserId(userDetails);
        List<Task> completedTasks = taskRepository.findByUserIdAndStatus(userId, Task.TaskStatus.COMPLETED);
        List<Task> pendingTasks = taskRepository.findByUserIdAndStatus(userId, Task.TaskStatus.PENDING);
        
        String analysis = aiService.analyzeProductivity(completedTasks, pendingTasks);
        return ResponseEntity.ok(Map.of("analysis", analysis));
    }

    private Long getUserId(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }
}

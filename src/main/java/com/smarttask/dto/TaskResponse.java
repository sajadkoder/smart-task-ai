package com.smarttask.dto;

import com.smarttask.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private Task.TaskStatus status;
    private Task.TaskPriority priority;
    private Task.TaskCategory category;
    private LocalDateTime dueDate;
    private LocalDateTime completedAt;
    private String aiSummary;
    private String aiSuggestion;
    private Integer position;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TaskResponse fromEntity(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .category(task.getCategory())
                .dueDate(task.getDueDate())
                .completedAt(task.getCompletedAt())
                .aiSummary(task.getAiSummary())
                .aiSuggestion(task.getAiSuggestion())
                .position(task.getPosition())
                .userId(task.getUser().getId())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}

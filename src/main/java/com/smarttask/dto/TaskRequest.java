package com.smarttask.dto;

import com.smarttask.entity.Task;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private Task.TaskStatus status;

    private Task.TaskPriority priority;

    private Task.TaskCategory category;

    private LocalDateTime dueDate;

    private Integer position;
}

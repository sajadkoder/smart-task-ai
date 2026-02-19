package com.smarttask.service;

import com.smarttask.dto.TaskResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyTaskUpdate(Long userId, TaskResponse task) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/tasks",
                task
        );
    }

    public void notifyTaskCreated(Long userId, TaskResponse task) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/tasks/created",
                task
        );
    }

    public void notifyTaskDeleted(Long userId, Long taskId) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/tasks/deleted",
                taskId
        );
    }

    public void broadcastToAll(String destination, Object payload) {
        messagingTemplate.convertAndSend(destination, payload);
    }
}

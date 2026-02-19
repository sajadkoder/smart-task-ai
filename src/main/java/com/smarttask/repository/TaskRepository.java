package com.smarttask.repository;

import com.smarttask.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByUserId(Long userId, Pageable pageable);

    List<Task> findByUserIdAndStatus(Long userId, Task.TaskStatus status);

    List<Task> findByUserIdAndCategory(Long userId, Task.TaskCategory category);

    List<Task> findByUserIdAndPriority(Long userId, Task.TaskPriority priority);

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.dueDate < :date AND t.status != 'COMPLETED'")
    List<Task> findOverdueTasks(@Param("userId") Long userId, @Param("date") LocalDateTime date);

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.status = 'COMPLETED' ORDER BY t.completedAt DESC")
    List<Task> findRecentlyCompletedTasks(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.user.id = :userId AND t.status = :status")
    Long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Task.TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId ORDER BY t.position ASC")
    List<Task> findByUserIdOrderByPositionAsc(@Param("userId") Long userId);
}

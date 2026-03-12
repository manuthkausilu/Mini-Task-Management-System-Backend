package com.example.mini_task.service;

import com.example.mini_task.dto.TaskRequestDTO;
import com.example.mini_task.dto.TaskResponseDTO;
import com.example.mini_task.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface TaskService{

    /**
     * Create a new task for authenticated user
     */
    TaskResponseDTO createTask(TaskRequestDTO request, Long userId);

    /**
     * Get all tasks with pagination, filtering, and sorting for authenticated user
     */
    @Transactional(readOnly = true)
    Page<TaskResponseDTO> getAllTasks(
            Task.TaskStatus status,
            Task.TaskPriority priority,
            Long userId,
            boolean isAdmin,
            Pageable pageable);

    /**
     * Get a single task by id for authenticated user
     */
    @Transactional(readOnly = true)
    TaskResponseDTO getTaskById(Long id, Long userId);

    /**
     * Update a task for authenticated user
     */
    TaskResponseDTO updateTask(Long id, TaskRequestDTO request, Long userId);

    /**
     * Delete a task for authenticated user
     */
    void deleteTask(Long id, Long userId);

    /**
     * Mark a task as completed for authenticated user
     */
    TaskResponseDTO markTaskAsCompleted(Long id, Long userId);
}

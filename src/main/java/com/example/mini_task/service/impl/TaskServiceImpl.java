package com.example.mini_task.service.impl;

import com.example.mini_task.dto.TaskRequestDTO;
import com.example.mini_task.dto.TaskResponseDTO;
import com.example.mini_task.entity.Task;
import com.example.mini_task.exception.ResourceNotFoundException;
import com.example.mini_task.repo.TaskRepository;
import com.example.mini_task.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    /**
     * Create a new task
     */
    public TaskResponseDTO createTask(TaskRequestDTO request, Long userId) {
        log.info("Creating new task with title: {} for user: {}", request.getTitle(), userId);

        Task task = Task.builder()
                .userId(userId)
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : Task.TaskStatus.TODO)
                .priority(request.getPriority() != null ? request.getPriority() : Task.TaskPriority.MEDIUM)
                .dueDate(request.getDueDate())
                .build();

        Task savedTask = taskRepository.save(task);
        log.info("Task created successfully with id: {}", savedTask.getId());

        return TaskResponseDTO.fromEntity(savedTask);
    }

    /**
     * Get all tasks with pagination, filtering, and sorting
     */
    @Transactional(readOnly = true)
    public Page<TaskResponseDTO> getAllTasks(
            Task.TaskStatus status,
            Task.TaskPriority priority,
            Long userId,
            Pageable pageable) {

        log.info("Fetching all tasks for user: {} with status: {}, priority: {}", userId, status, priority);

        Page<Task> tasks;

        if (status != null && priority != null) {
            tasks = taskRepository.findTasksByStatusAndPriority(status, priority, userId, pageable);
        } else if (status != null) {
            tasks = taskRepository.findTasksByStatus(status, userId, pageable);
        } else if (priority != null) {
            tasks = taskRepository.findTasksByPriority(priority, userId, pageable);
        } else {
            tasks = taskRepository.findAllByUserId(userId, pageable);
        }

        return tasks.map(TaskResponseDTO::fromEntity);
    }

    /**
     * Get a single task by id
     */
    @Transactional(readOnly = true)
    public TaskResponseDTO getTaskById(Long id, Long userId) {
        log.info("Fetching task with id: {} for user: {}", id, userId);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Task not found with id: {}", id);
                    return new ResourceNotFoundException("Task not found with id: " + id);
                });

        // Verify task belongs to user
        if (!task.getUserId().equals(userId)) {
            log.error("User: {} is not authorized to access task: {}", userId, id);
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }

        return TaskResponseDTO.fromEntity(task);
    }

    /**
     * Update a task
     */
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO request, Long userId) {
        log.info("Updating task with id: {} for user: {}", id, userId);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Task not found with id: {}", id);
                    return new ResourceNotFoundException("Task not found with id: " + id);
                });

        // Verify task belongs to user
        if (!task.getUserId().equals(userId)) {
            log.error("User: {} is not authorized to update task: {}", userId, id);
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());

        Task updatedTask = taskRepository.save(task);
        log.info("Task updated successfully with id: {}", id);

        return TaskResponseDTO.fromEntity(updatedTask);
    }

    /**
     * Delete a task
     */
    public void deleteTask(Long id, Long userId) {
        log.info("Deleting task with id: {} for user: {}", id, userId);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Task not found with id: {}", id);
                    return new ResourceNotFoundException("Task not found with id: " + id);
                });

        // Verify task belongs to user
        if (!task.getUserId().equals(userId)) {
            log.error("User: {} is not authorized to delete task: {}", userId, id);
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }

        taskRepository.delete(task);
        log.info("Task deleted successfully with id: {}", id);
    }

    /**
     * Mark a task as completed
     */
    public TaskResponseDTO markTaskAsCompleted(Long id, Long userId) {
        log.info("Marking task as completed with id: {} for user: {}", id, userId);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Task not found with id: {}", id);
                    return new ResourceNotFoundException("Task not found with id: " + id);
                });

        // Verify task belongs to user
        if (!task.getUserId().equals(userId)) {
            log.error("User: {} is not authorized to update task: {}", userId, id);
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }

        task.setStatus(Task.TaskStatus.DONE);
        Task updatedTask = taskRepository.save(task);
        log.info("Task marked as completed with id: {}", id);

        return TaskResponseDTO.fromEntity(updatedTask);
    }
}


package com.example.mini_task.controller;

import com.example.mini_task.dto.TaskRequestDTO;
import com.example.mini_task.dto.TaskResponseDTO;
import com.example.mini_task.entity.Task;
import com.example.mini_task.security.RoleChecker;
import com.example.mini_task.security.SecurityContextUtil;
import com.example.mini_task.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final TaskService taskService;

    /**
     * Create a new task
     * POST /api/v1/tasks
     */
    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskRequestDTO request) {
        log.info("Received request to create task");
        Long userId = SecurityContextUtil.getCurrentUserId();
        TaskResponseDTO response = taskService.createTask(request, userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get all tasks with pagination, filtering, and sorting
     * GET /api/v1/tasks?page=0&size=10&sort=dueDate,desc&status=TODO&priority=HIGH
     */
    @GetMapping
    public ResponseEntity<Page<TaskResponseDTO>> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) Task.TaskStatus status,
            @RequestParam(required = false) Task.TaskPriority priority) {

        log.info("Received request to get all tasks with filters - status: {}, priority: {}", status, priority);

        Long userId = SecurityContextUtil.getCurrentUserId();
        boolean isAdmin = RoleChecker.isAdmin();
        Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<TaskResponseDTO> response = taskService.getAllTasks(status, priority, userId, isAdmin, pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get a single task by id
     * GET /api/v1/tasks/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long id) {
        log.info("Received request to get task with id: {}", id);
        Long userId = SecurityContextUtil.getCurrentUserId();
        TaskResponseDTO response = taskService.getTaskById(id, userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Update a task
     * PUT /api/v1/tasks/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequestDTO request) {

        log.info("Received request to update task with id: {}", id);
        Long userId = SecurityContextUtil.getCurrentUserId();
        TaskResponseDTO response = taskService.updateTask(id, request, userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Delete a task
     * DELETE /api/v1/tasks/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        log.info("Received request to delete task with id: {}", id);
        Long userId = SecurityContextUtil.getCurrentUserId();
        taskService.deleteTask(id, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Mark a task as completed
     * PATCH /api/v1/tasks/{id}/complete
     */
    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskResponseDTO> markTaskAsCompleted(@PathVariable Long id) {
        log.info("Received request to mark task as completed with id: {}", id);
        Long userId = SecurityContextUtil.getCurrentUserId();
        TaskResponseDTO response = taskService.markTaskAsCompleted(id, userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}


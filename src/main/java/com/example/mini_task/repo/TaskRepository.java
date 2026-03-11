package com.example.mini_task.repo;

import com.example.mini_task.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE t.userId = ?3 AND " +
            "(?1 IS NULL OR t.status = ?1) AND " +
            "(?2 IS NULL OR t.priority = ?2)")
    Page<Task> findTasksByStatusAndPriority(
            Task.TaskStatus status,
            Task.TaskPriority priority,
            Long userId,
            Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.userId = ?2 AND (?1 IS NULL OR t.status = ?1)")
    Page<Task> findTasksByStatus(Task.TaskStatus status, Long userId, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.userId = ?2 AND (?1 IS NULL OR t.priority = ?1)")
    Page<Task> findTasksByPriority(Task.TaskPriority priority, Long userId, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.userId = ?1")
    Page<Task> findAllByUserId(Long userId, Pageable pageable);
}


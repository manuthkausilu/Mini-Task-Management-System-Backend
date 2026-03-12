package com.example.mini_task.service;

import com.example.mini_task.dto.auth.AuthResponse;
import com.example.mini_task.dto.auth.RegisterRequest;
import com.example.mini_task.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface AdminUserService {

    /**
     * Register a new admin user.
     */
    AuthResponse registerAdmin(RegisterRequest request);

    /**
     * Get all users with pagination.
     */
    @Transactional(readOnly = true)
    Page<AuthResponse> getAllUsers(Pageable pageable);

    /**
     * Get a single user by id.
     */
    @Transactional(readOnly = true)
    AuthResponse getUserById(Long userId);

    /**
     * Change a user's role.
     */
    AuthResponse changeUserRole(Long userId, User.Role role);

    /**
     * Delete a user.
     */
    void deleteUser(Long userId);

    /**
     * Get the current authenticated admin.
     */
    @Transactional(readOnly = true)
    AuthResponse getCurrentAdminInfo();
}


package com.example.mini_task.controller;

import com.example.mini_task.dto.auth.AuthResponse;
import com.example.mini_task.dto.auth.RegisterRequest;
import com.example.mini_task.entity.User;
import com.example.mini_task.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    /**
     * Register a new admin user (ADMIN only)
     * POST /api/v1/admin/users/register
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerAdmin(@Valid @RequestBody RegisterRequest request) {
        log.info("Received admin registration request for email: {}", request.getEmail());
        AuthResponse response = adminUserService.registerAdmin(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get all users (ADMIN only)
     * GET /api/v1/admin/users?page=0&size=10
     */
    @GetMapping
    public ResponseEntity<Page<AuthResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Received request to get all users - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<AuthResponse> responses = adminUserService.getAllUsers(pageable);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    /**
     * Get user by ID (ADMIN only)
     * GET /api/v1/admin/users/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<AuthResponse> getUserById(@PathVariable Long userId) {
        log.info("Received request to get user with id: {}", userId);
        AuthResponse response = adminUserService.getUserById(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Change user role (ADMIN only)
     * PATCH /api/v1/admin/users/{userId}/role
     */
    @PatchMapping("/{userId}/role")
    public ResponseEntity<AuthResponse> changeUserRole(
            @PathVariable Long userId,
            @RequestParam User.Role role) {

        log.info("Received request to change user role - userId: {}, newRole: {}", userId, role);
        AuthResponse response = adminUserService.changeUserRole(userId, role);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Delete user (ADMIN only)
     * DELETE /api/v1/admin/users/{userId}
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        log.info("Received request to delete user with id: {}", userId);
        adminUserService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Get current admin user info
     * GET /api/v1/admin/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentAdminInfo() {
        log.info("Received request to get current admin info");
        AuthResponse response = adminUserService.getCurrentAdminInfo();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}


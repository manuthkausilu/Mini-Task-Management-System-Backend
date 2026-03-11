package com.example.mini_task.controller;

import com.example.mini_task.dto.auth.AuthResponse;
import com.example.mini_task.dto.auth.RegisterRequest;
import com.example.mini_task.entity.User;
import com.example.mini_task.exception.ApiException;
import com.example.mini_task.repo.UserRepository;
import com.example.mini_task.security.RoleChecker;
import com.example.mini_task.service.AuthenticationService;
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

    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;

    /**
     * Register a new admin user (ADMIN only)
     * POST /api/v1/admin/users/register
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerAdmin(@Valid @RequestBody RegisterRequest request) {
        log.info("Admin requested admin registration for email: {}", request.getEmail());
        AuthResponse response = authenticationService.registerAdmin(request);
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

        log.info("Admin retrieving all users - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findAll(pageable);

        Page<AuthResponse> responses = users.map(user -> AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .build());

        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    /**
     * Get user by ID (ADMIN only)
     * GET /api/v1/admin/users/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<AuthResponse> getUserById(@PathVariable Long userId) {

        log.info("Admin retrieving user with id: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new com.example.mini_task.exception.ResourceNotFoundException("User not found with id: " + userId);
                });

        AuthResponse response = AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .build();

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

        log.info("Admin changing user role - userId: {}, newRole: {}", userId, role);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new com.example.mini_task.exception.ResourceNotFoundException("User not found with id: " + userId);
                });

        long adminCount = userRepository.countByRole(User.Role.ADMIN);
        if (user.getRole() == User.Role.ADMIN && role == User.Role.USER && adminCount <= 1) {
            throw new ApiException("Cannot demote the last admin user");
        }

        user.setRole(role);
        User updatedUser = userRepository.save(user);

        log.info("User role changed successfully - userId: {}, newRole: {}", userId, role);

        AuthResponse response = AuthResponse.builder()
                .userId(updatedUser.getId())
                .email(updatedUser.getEmail())
                .firstName(updatedUser.getFirstName())
                .lastName(updatedUser.getLastName())
                .role(updatedUser.getRole())
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Delete user (ADMIN only)
     * DELETE /api/v1/admin/users/{userId}
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {

        log.info("Admin deleting user with id: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new com.example.mini_task.exception.ResourceNotFoundException("User not found with id: " + userId);
                });

        User currentAdmin = RoleChecker.getCurrentUser();
        if (currentAdmin != null && currentAdmin.getId().equals(userId)) {
            throw new ApiException("Admin cannot delete own account");
        }

        long adminCount = userRepository.countByRole(User.Role.ADMIN);
        if (user.getRole() == User.Role.ADMIN && adminCount <= 1) {
            throw new ApiException("Cannot delete the last admin user");
        }

        userRepository.delete(user);

        log.info("User deleted successfully with id: {}", userId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Get current admin user info
     * GET /api/v1/admin/me
     */
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentAdminInfo() {

        User currentUser = RoleChecker.getCurrentUser();
        if (currentUser == null) {
            throw new com.example.mini_task.exception.AuthenticationException("Current user not found");
        }

        log.info("Admin retrieving own info - userId: {}", currentUser.getId());

        AuthResponse response = AuthResponse.builder()
                .userId(currentUser.getId())
                .email(currentUser.getEmail())
                .firstName(currentUser.getFirstName())
                .lastName(currentUser.getLastName())
                .role(currentUser.getRole())
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}


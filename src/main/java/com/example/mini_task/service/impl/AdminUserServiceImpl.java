package com.example.mini_task.service.impl;

import com.example.mini_task.dto.auth.AuthResponse;
import com.example.mini_task.dto.auth.RegisterRequest;
import com.example.mini_task.entity.User;
import com.example.mini_task.exception.ApiException;
import com.example.mini_task.exception.AuthenticationException;
import com.example.mini_task.exception.ResourceNotFoundException;
import com.example.mini_task.repo.UserRepository;
import com.example.mini_task.security.RoleChecker;
import com.example.mini_task.service.AdminUserService;
import com.example.mini_task.service.AuthenticationService;
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
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;

    @Override
    public AuthResponse registerAdmin(RegisterRequest request) {
        log.info("Admin requested admin registration for email: {}", request.getEmail());
        return authenticationService.registerAdmin(request);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuthResponse> getAllUsers(Pageable pageable) {
        log.info("Admin retrieving all users - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return userRepository.findAll(pageable)
                .map(this::mapToAuthResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse getUserById(Long userId) {
        log.info("Admin retrieving user with id: {}", userId);
        return mapToAuthResponse(getUserEntityById(userId));
    }

    @Override
    public AuthResponse changeUserRole(Long userId, User.Role role) {
        log.info("Admin changing user role - userId: {}, newRole: {}", userId, role);

        User user = getUserEntityById(userId);

        long adminCount = userRepository.countByRole(User.Role.ADMIN);
        if (user.getRole() == User.Role.ADMIN && role == User.Role.USER && adminCount <= 1) {
            throw new ApiException("Cannot demote the last admin user");
        }

        user.setRole(role);
        User updatedUser = userRepository.save(user);

        log.info("User role changed successfully - userId: {}, newRole: {}", userId, role);
        return mapToAuthResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("Admin deleting user with id: {}", userId);

        User user = getUserEntityById(userId);
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
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse getCurrentAdminInfo() {
        User currentUser = RoleChecker.getCurrentUser();
        if (currentUser == null) {
            throw new AuthenticationException("Current user not found");
        }

        log.info("Admin retrieving own info - userId: {}", currentUser.getId());
        return mapToAuthResponse(currentUser);
    }

    private User getUserEntityById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new ResourceNotFoundException("User not found with id: " + userId);
                });
    }

    private AuthResponse mapToAuthResponse(User user) {
        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .build();
    }
}


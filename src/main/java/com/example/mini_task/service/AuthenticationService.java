package com.example.mini_task.service;

import com.example.mini_task.dto.auth.AuthResponse;
import com.example.mini_task.dto.auth.LoginRequest;
import com.example.mini_task.dto.auth.RegisterRequest;

public interface AuthenticationService {

    /**
     * Register a new user
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Register a new admin user
     */
    AuthResponse registerAdmin(RegisterRequest request);

    /**
     * Authenticate user and return JWT token
     */
    AuthResponse login(LoginRequest request);
}


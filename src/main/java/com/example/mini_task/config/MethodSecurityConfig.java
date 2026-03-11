package com.example.mini_task.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true
)
public class MethodSecurityConfig {
    // This configuration enables @PreAuthorize, @PostAuthorize, @Secured, and @RolesAllowed annotations
}


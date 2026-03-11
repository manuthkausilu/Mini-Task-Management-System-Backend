package com.example.mini_task.config;

import com.example.mini_task.entity.User;
import com.example.mini_task.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultAdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin.enabled}")
    private boolean bootstrapAdminEnabled;

    @Value("${app.bootstrap.admin.first-name}")
    private String firstName;

    @Value("${app.bootstrap.admin.last-name}")
    private String lastName;

    @Value("${app.bootstrap.admin.email}")
    private String email;

    @Value("${app.bootstrap.admin.password}")
    private String password;

    @Override
    @Transactional
    public void run(@NonNull ApplicationArguments args) {
        if (!bootstrapAdminEnabled) {
            log.info("Bootstrap admin creation is disabled.");
            return;
        }

        if (userRepository.countByRole(User.Role.ADMIN) > 0) {
            log.info("Admin user already exists. Skipping bootstrap admin creation.");
            return;
        }

        if (userRepository.existsByEmail(email)) {
            log.warn("Bootstrap admin email '{}' already exists but is not ADMIN. Skipping auto-creation.", email);
            return;
        }

        User adminUser = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(User.Role.ADMIN)
                .build();

        userRepository.save(adminUser);
        log.warn("Default admin created with email '{}'. Change this password immediately.", email);
    }
}


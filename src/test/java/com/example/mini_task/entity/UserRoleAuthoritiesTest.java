package com.example.mini_task.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserRoleAuthoritiesTest {

    @Test
    void shouldExposeAdminAuthorityFromRole() {
        User admin = User.builder()
                .email("admin@example.com")
                .firstName("Admin")
                .lastName("User")
                .password("secret")
                .role(User.Role.ADMIN)
                .build();

        String authority = admin.getAuthorities().iterator().next().getAuthority();

        assertEquals("ROLE_ADMIN", authority);
    }

    @Test
    void shouldExposeUserAuthorityFromRole() {
        User user = User.builder()
                .email("user@example.com")
                .firstName("Normal")
                .lastName("User")
                .password("secret")
                .role(User.Role.USER)
                .build();

        String authority = user.getAuthorities().iterator().next().getAuthority();

        assertEquals("ROLE_USER", authority);
    }
}


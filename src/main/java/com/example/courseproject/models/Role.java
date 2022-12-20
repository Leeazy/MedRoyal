package com.example.courseproject.models;

import org.springframework.security.core.GrantedAuthority;

// Роль пользователя в системе
public enum Role implements GrantedAuthority {
    USER,
    ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}

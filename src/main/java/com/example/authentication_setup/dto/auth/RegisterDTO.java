package com.example.authentication_setup.dto.auth;

import com.example.authentication_setup.entitty.user.UserRole;

public record RegisterDTO(String email, String password, UserRole role, String name) {
    
}

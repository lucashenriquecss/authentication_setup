package com.example.authentication_setup.entitty.user;

public record RegisterDTO(String email, String password, UserRole role, String name) {
    
}

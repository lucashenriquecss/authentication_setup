package com.example.authentication_setup.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.authentication_setup.entitty.user.User;

public interface UserRepository extends JpaRepository<User, String> {
        UserDetails findByEmail(String email);
}

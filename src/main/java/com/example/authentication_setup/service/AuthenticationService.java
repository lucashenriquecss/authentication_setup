package com.example.authentication_setup.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;

import com.example.authentication_setup.repository.UserRepository;
import com.example.authentication_setup.exception.auth.*;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements UserDetailsService {

    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            log.warn("User not found with email: {}", email);
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return user;
    }

    public LoginResponseDTO authenticate(AuthenticationDTO credentials) {
        try {
            var authToken = new UsernamePasswordAuthenticationToken(
                credentials.email(), 
                credentials.password()
            );

            var auth = authenticationManager.authenticate(authToken);

            return tokenService.generateToken((User) auth.getPrincipal());
        } catch (BadCredentialsException e) {
            throw new AuthenticationFailedException("Invalid credentials");
        }
    }

    public LoginResponseDTO refreshToken(String refreshToken) {
        try {
            return tokenService.generateRefreshToken(refreshToken);
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid or expired refresh token");
        }
    }

    @Transactional
    public void registerUser(RegisterDTO registrationData) {
        validateRegistrationData(registrationData);

        if (userRepository.findByEmail(registrationData.email()) != null) {
            throw new UserAlreadyExistsException("Email already registered");
        }

        User newUser = User.builder()
            .email(registrationData.email())
            .password(passwordEncoder.encode(registrationData.password()))
            .role(registrationData.role())
            .name(registrationData.name())
            .createdAt(LocalDateTime.now())
            .isActive(true)
            .build();

        userRepository.save(newUser);
        
    }

    public void initiatePasswordRecovery(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
    }
    
}

package com.example.authentication_setup.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.example.authentication_setup.dto.auth.AuthenticationDTO;
import com.example.authentication_setup.dto.auth.LoginResponseDTO;
import com.example.authentication_setup.dto.auth.RegisterDTO;
import com.example.authentication_setup.entitty.user.User;
import com.example.authentication_setup.entitty.user.UserRole;
import com.example.authentication_setup.exception.auth.AuthenticationFailedException;
import com.example.authentication_setup.exception.auth.UserAlreadyExistsException;
import com.example.authentication_setup.infra.security.TokenService;
import com.example.authentication_setup.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private TokenService tokenService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthenticationService authenticationService;

    private User testUser;
    private RegisterDTO validRegistrationDTO;
    private AuthenticationDTO validAuthDTO;
    private LoginResponseDTO expectedLoginResponse;

   @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = User.builder()
                .id("sdsdsdsdsdsdsdsdsds")
                .email("test@example.com")
                .password("encryptedPassword")
                .name("Test User")
                .role(UserRole.USER)
                .isActive(true)
                .build();


        validRegistrationDTO = new RegisterDTO(
                "test@example.com",
                "password123",
                UserRole.USER,
                "Test User"
        );

        validAuthDTO = new AuthenticationDTO(
                "test@example.com",
                "password123"
        );

        expectedLoginResponse = new LoginResponseDTO(
                "access_token",
                "refresh_token"
        );
    }

    @Test
    void authenticate_Success() {
        Authentication mockAuth = mock(Authentication.class);

        when(mockAuth.getPrincipal()).thenReturn(testUser);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuth);

        when(tokenService.generateToken(testUser)).thenReturn(expectedLoginResponse);

        LoginResponseDTO response = authenticationService.authenticate(validAuthDTO);

        assertNotNull(response);
        assertEquals(expectedLoginResponse.accessToken(), response.accessToken());
        assertEquals(expectedLoginResponse.refreshToken(), response.refreshToken());
    }
    
    @Test
    void authenticate_InvalidCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new BadCredentialsException("Invalid credentials"));


        assertThrows(AuthenticationFailedException.class, () -> 
        authenticationService.authenticate(validAuthDTO),
        "Expected AuthenticationFailedException when credentials are invalid"
    );
    
    }
    @Test
    void registerUser_Success() {
        when(userRepository.findByEmail(validRegistrationDTO.email())).thenReturn(null);

        when(passwordEncoder.encode(validRegistrationDTO.password())).thenReturn("encryptedPassword");

        assertDoesNotThrow(() -> authenticationService.registerUser(validRegistrationDTO));

        verify(userRepository).save(any(User.class));

    }

    @Test
    void registerUser_EmailAlreadyExists() {
        when(userRepository.findByEmail(validRegistrationDTO.email())).thenReturn(testUser);

        assertThrows(UserAlreadyExistsException.class, () ->
            authenticationService.registerUser(validRegistrationDTO)
        );

        verify(userRepository, never()).save(any(User.class));
    }
}

package com.example.authentication_setup.controller;

import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.authentication_setup.dto.auth.*;
import com.example.authentication_setup.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication management endpoints")
@RequiredArgsConstructor
public class AuthenticationController {
    
    private final AuthenticationService authService;

    @Operation(summary = "Authenticate user and generate tokens")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO credentials) {
        try {
            LoginResponseDTO response = authService.authenticate(credentials);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Refresh authentication token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token successfully refreshed"),
        @ApiResponse(responseCode = "401", description = "Invalid refresh token")
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponseDTO> refreshToken(@RequestBody @Valid RefreshTokenDTO refreshToken) {
        try {
            LoginResponseDTO response = authService.refreshToken(refreshToken.refreshToken());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Register new user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User successfully registered"),
        @ApiResponse(responseCode = "409", description = "User already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterDTO registrationData) {
        try {
            authService.registerUser(registrationData);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    // @Operation(summary = "Initialize password recovery process")
    // @PostMapping("/forgot-password")
    // public ResponseEntity<Void> initiatePasswordRecovery(@RequestBody @Valid ForgotPasswordDTO request) {
    //     try {
    //         authService.initiatePasswordRecovery(request.email());
    //         return ResponseEntity.ok().build();
    //     } catch (IllegalArgumentException e) {
    //         return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    //     }
    // }
}
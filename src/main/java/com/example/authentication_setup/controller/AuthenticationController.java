package com.example.authentication_setup.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.authentication_setup.dto.auth.AuthenticationDTO;
import com.example.authentication_setup.dto.auth.LoginResponseDTO;
import com.example.authentication_setup.dto.auth.RefreshTokenDTO;
import com.example.authentication_setup.dto.auth.RegisterDTO;
import com.example.authentication_setup.entitty.user.User;
import com.example.authentication_setup.infra.security.TokenService;
import com.example.authentication_setup.repository.UserRepository;

@RestController
@RequestMapping("api/v1/auth")
public class AuthenticationController {
     @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository repository;
    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data){
    
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var tokenResponse = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(tokenResponse);
        
    }

    @PostMapping("/refresh_token")
    public ResponseEntity<LoginResponseDTO> refreshToken(@RequestBody @Valid RefreshTokenDTO data) {
        var tokenResponse = tokenService.generateRefreshToken(data.refreshToken());
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO data){
        if(this.repository.findByEmail(data.email()) != null) return ResponseEntity.badRequest().build();

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        User newUser = new User(data.email(), encryptedPassword, data.role(), data.name());

        this.repository.save(newUser);

        return ResponseEntity.ok().build();
    }
    @PostMapping("/forgot_password")
    public ResponseEntity forgotPassoword(){
        return ResponseEntity.ok().build();
    }
}

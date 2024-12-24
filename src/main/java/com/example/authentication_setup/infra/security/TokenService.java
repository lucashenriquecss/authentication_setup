package com.example.authentication_setup.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.example.authentication_setup.entitty.user.User;
import com.auth0.jwt.exceptions.JWTVerificationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.example.authentication_setup.entitty.user.LoginResponseDTO;
import com.example.authentication_setup.repository.UserRepository;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;

    @Autowired
    private UserRepository userRepository;
    // @Value("${api.security.token.expiration}")
    // private Integer hourExpirationToken;

    // @Value("${api.security.refresh-token.secret}")
    // private Integer hourExpirationRefreshToken;

    public LoginResponseDTO generateToken(User user){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);

            String accessToken = JWT.create()
                    .withIssuer("auth-api")
                    .withSubject(user.getEmail())
                    .withExpiresAt(genExpirationDate(2))
                    .sign(algorithm);

            String refreshToken = JWT.create()
                    .withIssuer("auth-api")
                    .withSubject(user.getEmail())
                    .withExpiresAt(genExpirationDate(4))
                    .sign(algorithm);

            return new LoginResponseDTO(accessToken, refreshToken);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error while generating token", exception);
        }
    }

    public String validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception){
            throw new RuntimeException("UnauthorizedException",exception);
            
        }
    }

    public LoginResponseDTO generateRefreshToken(String refreshToken) {
        try {
           
            String email = validateToken(refreshToken);

            User user = (User) userRepository.findByEmail(email);
            if (user == null) {
                throw new RuntimeException("User not found");
            }
            
            return generateToken(user);
    
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Invalid Refresh Token", exception);
        }
    }

    private Instant genExpirationDate(Integer expiration){
        return LocalDateTime.now().plusHours(expiration).toInstant(ZoneOffset.of("-03:00"));
    }
}
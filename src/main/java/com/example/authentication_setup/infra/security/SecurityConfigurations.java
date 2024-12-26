package com.example.authentication_setup.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    @Autowired
    SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return  httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/refresh-token").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/forgot-password").permitAll()
                        // .requestMatchers(HttpMethod.GET, "/auth/forgot_password2").hasRole("ADMIN") //restricted route for admin
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

     @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}

/* 
 *  EN - Disabling default Spring Security settings and declaring your own settings
 *  PT-BR - Desabilitando configurações padrões do Spring Security e declarando configurações proprias 
 
 *  EN - All requests require authentication, this is declared with .anyRequest().authenticated()
 *  PT-BR - Todas as requisições precisam de autenticação, isso é declarado com .anyRequest().authenticated()
 
 *  EN - Use permitAll() to leave the route public, if I want to restrict a route to a specific role I use hasRole() 
 *  PT-BR - Uso do permitAll() para deixar a rota publica, caso eu queira restringir uma rota para uma role especifica utilizo o hasRole()
*/
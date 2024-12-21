package com.example.authentication_setup.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
public class AuthenticationController {
    
    @GetMapping
    public ResponseEntity getTest(){
        return ResponseEntity.ok("Ok");
    }
}

package com.example.demo.controller;

import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public String signup(@RequestBody AuthRequest request) {
        return authService.signup(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                request.getRole()
        );
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        String token = authService.login(
                request.getEmail(),
                request.getPassword()
        );
        return new AuthResponse(token);
    }
}
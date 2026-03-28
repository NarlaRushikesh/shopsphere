package com.example.demo.service.impl;

import com.example.demo.entity.User;
import com.example.demo.exception.InvalidCredentialsException;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public String signup(String name, String email, String password, String role) {
        // 409 if email already registered
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException(email);
        }

        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name must not be blank");
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("Email must not be blank");
        if (password == null || password.length() < 6)
            throw new IllegalArgumentException("Password must be at least 6 characters");

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role != null ? role.toUpperCase() : "USER");

        userRepository.save(user);
        return "User registered successfully";
    }

    @Override
    public String login(String email, String password) {
        // 404 if email not registered
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        // 401 if password wrong
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return JwtUtil.generateToken(user.getEmail(), user.getRole());
    }
}
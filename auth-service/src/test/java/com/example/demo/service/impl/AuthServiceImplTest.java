package com.example.demo.service.impl;

// FILE LOCATION:
// auth-service/src/test/java/com/example/demo/service/impl/AuthServiceImplTest.java

import com.example.demo.entity.User;
import com.example.demo.exception.InvalidCredentialsException;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    // ─────────────────────────────────────────────
    // signup()
    // ─────────────────────────────────────────────

    @Test
    void signup_success_returnsMessage() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret123")).thenReturn("hashed");

        String result = authService.signup("John", "john@example.com", "secret123", "USER");

        assertEquals("User registered successfully", result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void signup_defaultsRoleToUser_whenRoleIsNull() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");

        authService.signup("John", "john@example.com", "secret123", null);

        verify(userRepository).save(argThat(u -> "USER".equals(u.getRole())));
    }

    @Test
    void signup_uppercasesRole() {
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");

        authService.signup("Admin", "admin@example.com", "pass123", "admin");

        verify(userRepository).save(argThat(u -> "ADMIN".equals(u.getRole())));
    }

    @Test
    void signup_throwsUserAlreadyExistsException_whenEmailTaken() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistsException.class,
                () -> authService.signup("John", "john@example.com", "secret123", "USER"));

        verify(userRepository, never()).save(any());
    }

    @Test
    void signup_throwsIllegalArgument_whenNameIsBlank() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> authService.signup("  ", "john@example.com", "secret123", "USER"));
    }

    @Test
    void signup_throwsIllegalArgument_whenEmailIsBlank() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> authService.signup("John", "  ", "secret123", "USER"));
    }

    @Test
    void signup_throwsIllegalArgument_whenPasswordTooShort() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> authService.signup("John", "john@example.com", "abc", "USER"));
    }

    @Test
    void signup_throwsIllegalArgument_whenPasswordIsNull() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> authService.signup("John", "john@example.com", null, "USER"));
    }

    // ─────────────────────────────────────────────
    // login()
    // ─────────────────────────────────────────────

    @Test
    void login_success_returnsToken() {
        User user = new User();
        user.setEmail("john@example.com");
        user.setPassword("hashed");
        user.setRole("USER");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret123", "hashed")).thenReturn(true);

        try (MockedStatic<JwtUtil> jwtUtil = mockStatic(JwtUtil.class)) {
            jwtUtil.when(() -> JwtUtil.generateToken("john@example.com", "USER"))
                   .thenReturn("mocked.jwt.token");

            String token = authService.login("john@example.com", "secret123");

            assertEquals("mocked.jwt.token", token);
        }
    }

    @Test
    void login_throwsUserNotFoundException_whenEmailNotFound() {
        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> authService.login("nobody@example.com", "pass"));
    }

    @Test
    void login_throwsInvalidCredentialsException_whenPasswordWrong() {
        User user = new User();
        user.setEmail("john@example.com");
        user.setPassword("hashed");
        user.setRole("USER");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "hashed")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login("john@example.com", "wrongpass"));
    }
}
package com.example.demo.controller;

// FILE LOCATION:
// auth-service/src/test/java/com/example/demo/controller/AuthControllerTest.java

import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private AuthRequest buildRequest(String name, String email, String password, String role) {
        AuthRequest req = new AuthRequest();
        req.setName(name);
        req.setEmail(email);
        req.setPassword(password);
        req.setRole(role);
        return req;
    }

    // ─────────────────────────────────────────────
    // signup()
    // ─────────────────────────────────────────────

    @Test
    void signup_delegatesToService_andReturnsMessage() {
        AuthRequest req = buildRequest("John", "john@example.com", "secret123", "USER");
        when(authService.signup("John", "john@example.com", "secret123", "USER"))
                .thenReturn("User registered successfully");

        String result = authController.signup(req);

        assertEquals("User registered successfully", result);
        verify(authService).signup("John", "john@example.com", "secret123", "USER");
    }

    @Test
    void signup_propagatesException_fromService() {
        AuthRequest req = buildRequest("John", "john@example.com", "secret123", "USER");
        when(authService.signup(any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Email taken"));

        assertThrows(RuntimeException.class, () -> authController.signup(req));
    }

    // ─────────────────────────────────────────────
    // login()
    // ─────────────────────────────────────────────

    @Test
    void login_delegatesToService_andWrapsTokenInResponse() {
        AuthRequest req = buildRequest(null, "john@example.com", "secret123", null);
        when(authService.login("john@example.com", "secret123")).thenReturn("jwt.token.here");

        AuthResponse response = authController.login(req);

        assertNotNull(response);
        assertEquals("jwt.token.here", response.getToken());
    }

    @Test
    void login_propagatesException_fromService() {
        AuthRequest req = buildRequest(null, "bad@example.com", "wrong", null);
        when(authService.login(any(), any())).thenThrow(new RuntimeException("Invalid credentials"));

        assertThrows(RuntimeException.class, () -> authController.login(req));
    }
}
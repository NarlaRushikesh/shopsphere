package com.example.demo.security;

// FILE LOCATION:
// api-gateway/src/test/java/com/example/demo/security/JwtAuthenticationFilterTest.java

import com.example.demo.security.JwtAuthenticationFilter;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JwtAuthenticationFilterTest {

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @Mock
    private GatewayFilterChain chain;

    private static final String SECRET = "mysecretkeymysecretkeymysecretkey";
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    // ── helper to build a valid JWT ───────────────────────────────────────

    private String validToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(KEY)
                .compact();
    }

    private String expiredToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date(System.currentTimeMillis() - 120_000))
                .setExpiration(new Date(System.currentTimeMillis() - 60_000))
                .signWith(KEY)
                .compact();
    }

    // ─────────────────────────────────────────────
    // Public paths — no JWT required
    // ─────────────────────────────────────────────

    @Test
    void filter_allowsRequest_forLoginPath_withoutToken() {
        when(chain.filter(any())).thenReturn(Mono.empty());
        MockServerHttpRequest request = MockServerHttpRequest.get("/auth/login").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        filter.filter(exchange, chain).block();

        verify(chain).filter(any());
        assertNull(exchange.getResponse().getStatusCode());
    }

    @Test
    void filter_allowsRequest_forSignupPath_withoutToken() {
        when(chain.filter(any())).thenReturn(Mono.empty());
        MockServerHttpRequest request = MockServerHttpRequest.get("/auth/signup").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        filter.filter(exchange, chain).block();

        verify(chain).filter(any());
    }

    @Test
    void filter_allowsRequest_forSwaggerUiPath_withoutToken() {
        when(chain.filter(any())).thenReturn(Mono.empty());
        MockServerHttpRequest request = MockServerHttpRequest.get("/swagger-ui/index.html").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        filter.filter(exchange, chain).block();

        verify(chain).filter(any());
    }

    // ─────────────────────────────────────────────
    // Missing / malformed token
    // ─────────────────────────────────────────────

    @Test
    void filter_returns401_whenAuthorizationHeaderIsMissing() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/catalog/products").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        filter.filter(exchange, chain).block();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        verify(chain, never()).filter(any());
    }

    @Test
    void filter_returns401_whenBearerPrefixIsMissing() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/catalog/products")
                .header(HttpHeaders.AUTHORIZATION, "Basic abc123")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        filter.filter(exchange, chain).block();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void filter_returns401_whenTokenIsGibberish() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/catalog/products")
                .header(HttpHeaders.AUTHORIZATION, "Bearer not.a.real.token")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        filter.filter(exchange, chain).block();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void filter_returns401_whenTokenIsExpired() {
        String token = expiredToken("user@example.com", "USER");
        MockServerHttpRequest request = MockServerHttpRequest.get("/catalog/products")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        filter.filter(exchange, chain).block();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    // ─────────────────────────────────────────────
    // Valid USER token
    // ─────────────────────────────────────────────

    @Test
    void filter_allowsRequest_withValidUserToken_forCatalogPath() {
        when(chain.filter(any())).thenReturn(Mono.empty());
        String token = validToken("user@example.com", "USER");
        MockServerHttpRequest request = MockServerHttpRequest.get("/catalog/products")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        filter.filter(exchange, chain).block();

        verify(chain).filter(any());
    }

    // ─────────────────────────────────────────────
    // Admin path enforcement
    // ─────────────────────────────────────────────

    @Test
    void filter_returns403_whenUserTokenAccessesAdminPath() {
        String token = validToken("user@example.com", "USER");
        MockServerHttpRequest request = MockServerHttpRequest.get("/admin/products")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        filter.filter(exchange, chain).block();

        assertEquals(HttpStatus.FORBIDDEN, exchange.getResponse().getStatusCode());
        verify(chain, never()).filter(any());
    }

    @Test
    void filter_allowsRequest_whenAdminTokenAccessesAdminPath() {
        when(chain.filter(any())).thenReturn(Mono.empty());
        String token = validToken("admin@example.com", "ADMIN");
        MockServerHttpRequest request = MockServerHttpRequest.get("/admin/products")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        filter.filter(exchange, chain).block();

        verify(chain).filter(any());
    }

    // ─────────────────────────────────────────────
    // getOrder()
    // ─────────────────────────────────────────────

    @Test
    void filter_returnsOrderMinusOne() {
        assertEquals(-1, filter.getOrder());
    }
}
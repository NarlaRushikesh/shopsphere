package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final String SECRET = "mysecretkeymysecretkeymysecretkey";
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth/login",
            "/auth/signup",
            "/swagger-ui",
            "/webjars",
            "/auth/v3/api-docs",
            "/catalog/v3/api-docs",
            "/orders/v3/api-docs",
            "/v3/api-docs"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Let public paths through
        if (PUBLIC_PATHS.stream().anyMatch(path::contains)) {
            return chain.filter(exchange);
        }

        // Extract JWT
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return sendError(exchange, HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }

        // Validate JWT
        Claims claims;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(authHeader.substring(7))
                    .getBody();
        } catch (ExpiredJwtException e) {
            return sendError(exchange, HttpStatus.UNAUTHORIZED, "JWT token has expired");
        } catch (Exception e) {
            return sendError(exchange, HttpStatus.UNAUTHORIZED, "Invalid JWT token");
        }

        String role  = (String) claims.get("role");
        String email = claims.getSubject();

        // Block non-ADMIN users from /admin/** (including /admin/v3/api-docs)
        if (path.startsWith("/admin") && !"ADMIN".equalsIgnoreCase(role)) {
            return sendError(exchange, HttpStatus.FORBIDDEN, "Access denied: Admin role required");
        }

        // Forward user info as headers to downstream services
        // Also preserve the Authorization header so downstream Feign clients can forward it further
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header("X-User-Email", email)
                .header("X-User-Role",  role != null ? role : "")
                .header("Authorization", authHeader)   // ← preserve token for Feign forwarding
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private Mono<Void> sendError(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json");
        var buffer = response.bufferFactory()
                .wrap(("{\"error\":\"" + message + "\"}").getBytes());
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
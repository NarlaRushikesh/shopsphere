package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class JwtUtil {

    private static final String SECRET = "mysecretkeymysecretkeymysecretkey";
    private static final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    // ✅ Extract Email safely
    public static String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    // ✅ Extract Role safely
    public static String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    // 🔥 COMMON METHOD (IMPORTANT FIX)
    private static Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (ExpiredJwtException e) {
            throw e; // let JwtFilter handle → return 401

        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT Token");
        }
    }
}
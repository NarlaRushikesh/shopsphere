package com.shopsphere.catalog.config;

import com.shopsphere.catalog.security.JwtFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Catalog service security.
 *
 * Category write endpoints (POST/PUT/DELETE /categories/**) have been removed
 * from this service — they are only accessible through admin-service.
 * All remaining category endpoints are public reads.
 *
 * Product write endpoints still require ADMIN role because admin-service
 * forwards a valid admin JWT via Feign.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public JwtFilter jwtFilter() { return new JwtFilter(); }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex.authenticationEntryPoint((req, res, e) ->
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                // ── Public read endpoints ────────────────────────────────
                .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/categories/**").permitAll()

                // ── Product writes — ADMIN only (called via admin-service Feign) ──
                .requestMatchers(HttpMethod.POST,   "/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH,  "/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/products/**").hasRole("ADMIN")

                // ── Category writes removed from this service entirely ───
                // POST/PUT/DELETE /categories/** are not exposed here.
                // They are routed through admin-service only.

                .anyRequest().denyAll()
            )
            .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
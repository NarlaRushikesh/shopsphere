package com.example.demo.config;

import com.example.demo.security.JwtFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Order service security.
 *
 * User endpoints (/cart, /place, /my) require authentication.
 * Admin-only endpoints (/all, /{id}/status) require ADMIN role —
 * these are called internally by admin-service via Feign with a
 * forwarded admin JWT, and are blocked for regular users.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex.authenticationEntryPoint((req, res, e) ->
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/v3/api-docs"
                ).permitAll()

                // ── User endpoints ───────────────────────────────────────
                .requestMatchers("/cart", "/cart/**").authenticated()
                .requestMatchers("/place", "/place/**").authenticated()
                .requestMatchers("/my", "/my/**").authenticated()

                // ── Admin-only endpoints (called via admin-service Feign) ─
                .requestMatchers(HttpMethod.GET, "/all").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/{id}/status").hasRole("ADMIN")

                .anyRequest().denyAll()
            )
            .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
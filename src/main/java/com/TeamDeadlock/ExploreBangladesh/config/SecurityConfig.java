package com.TeamDeadlock.ExploreBangladesh.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration to allow Hotels API access without authentication
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configure(http)) // Enable CORS
            .csrf(csrf -> csrf.disable()) // Disable CSRF for API endpoints
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").permitAll() // Allow all API endpoints
                .requestMatchers("/hotels.html", "/flights.html", "/js/**", "/css/**", "/images/**", "/styles.css").permitAll() // Allow static resources
                .requestMatchers("/", "/index.html", "/login.html", "/signup.html").permitAll() // Allow public pages
                .anyRequest().permitAll() // Allow all requests for now
            );
        
        return http.build();
    }
}

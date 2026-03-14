package com.TeamDeadlock.ExploreBangladesh.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * CORS Configuration to allow requests from Live Server (VS Code extension)
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.front-end-url:http://127.0.0.1:5500,http://127.0.0.1:5501,http://localhost:5500,http://localhost:5501,http://localhost:8080}")
            String corsUrls
    ) {
        CorsConfiguration configuration = new CorsConfiguration();

        String[] urls = corsUrls.trim().split(",");
        configuration.setAllowedOrigins(List.of(urls));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}

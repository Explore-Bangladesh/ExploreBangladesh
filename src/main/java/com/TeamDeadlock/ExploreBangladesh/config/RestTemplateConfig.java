package com.TeamDeadlock.ExploreBangladesh.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate Configuration
 * Provides RestTemplate bean for making HTTP requests to external APIs
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(java.time.Duration.ofSeconds(30))
                .setReadTimeout(java.time.Duration.ofSeconds(120))
                .build();
    }
}

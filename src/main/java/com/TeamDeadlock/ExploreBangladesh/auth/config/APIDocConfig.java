package com.TeamDeadlock.ExploreBangladesh.auth.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Auth Application",
                description = "Generic auth app with JWT access/refresh support.",
                contact = @Contact(
                        name = "TeamDeadlock",
                        url = "https://www.substringtechnologies.com/",
                        email = "support@substringtechnologies.com"
                ),
                version = "1.0",
                summary = "Authentication API documentation"
        ),
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class APIDocConfig {
}

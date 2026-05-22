package com.jciphertools.infrastructure.config.properties;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

@Validated
@ConfigurationProperties(prefix = "cors")
public record CorsProperties(@NotEmpty(message = "CORS_ALLOWED_ORIGINS must be provided")
                             List<@NotBlank(message = "CORS_ALLOWED_ORIGINS cannot contain blank values") String> allowedOrigins) {
}

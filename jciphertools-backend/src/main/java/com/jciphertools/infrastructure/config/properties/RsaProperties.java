package com.jciphertools.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@Validated
@ConfigurationProperties(prefix = "cipher.rsa")
public record RsaProperties(
        @NotBlank(message = "RSA_PUBLIC_KEY_PATH must be provided") String publicKeyPath,
        @NotBlank(message = "RSA_PRIVATE_KEY_PATH must be provided") String privateKeyPath) {
}

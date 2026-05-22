package com.jciphertools.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@Validated
@ConfigurationProperties(prefix = "cipher.aes.gcm")
public record AesGcmProperties(
        @NotBlank(message = "AES_GCM_KEY must be provided") String key,
        @NotBlank(message = "AES_GCM_IV must be provided") String iv) {
}

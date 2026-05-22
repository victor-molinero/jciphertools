package com.jciphertools.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@Validated
@ConfigurationProperties(prefix = "cipher.aes.cbc")
public record AesCbcProperties(
        @NotBlank(message = "AES_CBC_KEY must be provided") String key,
        @NotBlank(message = "AES_CBC_IV must be provided") String iv) {
}

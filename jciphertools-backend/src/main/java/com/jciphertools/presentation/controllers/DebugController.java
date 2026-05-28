package com.jciphertools.presentation.controllers;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jciphertools.infrastructure.config.properties.AesCbcProperties;
import com.jciphertools.infrastructure.config.properties.AesGcmProperties;
import com.jciphertools.infrastructure.config.properties.CorsProperties;
import com.jciphertools.infrastructure.config.properties.RsaProperties;
import com.jciphertools.presentation.dto.EnvironmentCheckResponseDto;

@RestController
@RequestMapping("/api/v1/debug")
public class DebugController {

    private static final Logger log = LoggerFactory.getLogger(DebugController.class);

    private final AesCbcProperties aesCbcProperties;
    private final AesGcmProperties aesGcmProperties;
    private final RsaProperties rsaProperties;
    private final CorsProperties corsProperties;

    public DebugController(AesCbcProperties aesCbcProperties, AesGcmProperties aesGcmProperties,
            RsaProperties rsaProperties, CorsProperties corsProperties) {
        this.aesCbcProperties = aesCbcProperties;
        this.aesGcmProperties = aesGcmProperties;
        this.rsaProperties = rsaProperties;
        this.corsProperties = corsProperties;
    }

    @GetMapping("/env")
    public ResponseEntity<EnvironmentCheckResponseDto> checkEnvironment() {
        Map<String, Boolean> checks = new LinkedHashMap<>();
        checks.put("AES_CBC_KEY", isBase64WithDecodedLength(aesCbcProperties.key(), 32));
        checks.put("AES_CBC_IV", isBase64WithDecodedLength(aesCbcProperties.iv(), 16));
        checks.put("AES_GCM_KEY", isBase64WithDecodedLength(aesGcmProperties.key(), 32));
        checks.put("AES_GCM_IV", isBase64WithDecodedLength(aesGcmProperties.iv(), 12));
        checks.put("RSA_PUBLIC_KEY_PEM", isValidPublicKeyPem(rsaProperties.publicKeyPem()));
        checks.put("RSA_PRIVATE_KEY_PEM", isValidPrivateKeyPem(rsaProperties.privateKeyPem()));
        checks.put("CORS_ALLOWED_ORIGINS", isValidCorsOrigins(corsProperties.allowedOrigins()));

        boolean allValid = checks.values().stream().allMatch(Boolean::booleanValue);
        List<String> invalidKeys = checks.entrySet().stream()
                .filter(entry -> !entry.getValue())
                .map(Map.Entry::getKey)
                .toList();

        String summary = allValid
                ? "All required environment values are valid"
                : "Missing or invalid environment values: " + String.join(", ", invalidKeys);

        log.info("Environment validation completed: allValid={}, invalidKeys={}", allValid,
                invalidKeys.isEmpty() ? List.of("none") : invalidKeys);

        EnvironmentCheckResponseDto response = new EnvironmentCheckResponseDto(
                Instant.now().toString(),
                checks,
                allValid,
                summary);

        return ResponseEntity.status(allValid ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private boolean isBase64WithDecodedLength(String value, int expectedLength) {
        if (value == null || value.isBlank()) {
            return false;
        }

        try {
            return Base64.getDecoder().decode(value).length == expectedLength;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private boolean isValidPublicKeyPem(String value) {
        return isValidKeyPem(value, true);
    }

    private boolean isValidPrivateKeyPem(String value) {
        return isValidKeyPem(value, false);
    }

    private boolean isValidKeyPem(String value, boolean publicKey) {
        if (value == null || value.isBlank()) {
            return false;
        }

        try {
            String normalized = value.replaceAll("-----[A-Z ]+-----", "").replaceAll("\\s", "");
            byte[] der = Base64.getDecoder().decode(normalized);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            if (publicKey) {
                PublicKey parsed = keyFactory.generatePublic(new X509EncodedKeySpec(der));
                return parsed != null;
            }

            PrivateKey parsed = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(der));
            return parsed != null;
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean isValidCorsOrigins(List<String> origins) {
        if (origins == null || origins.isEmpty()) {
            return false;
        }

        return origins.stream().noneMatch(origin -> origin == null || origin.isBlank());
    }
}
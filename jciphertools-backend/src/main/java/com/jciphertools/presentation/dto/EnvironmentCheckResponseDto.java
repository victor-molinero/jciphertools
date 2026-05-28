package com.jciphertools.presentation.dto;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Result of a runtime environment validation check")
public record EnvironmentCheckResponseDto(
        @Schema(description = "Time the check was executed in ISO-8601 format")
        String timestamp,
        @Schema(description = "Per-setting validation status keyed by setting name")
        Map<String, Boolean> checks,
        @Schema(description = "Whether all required settings are valid")
        boolean allValid,
        @Schema(description = "Human-readable summary of the validation result")
        String summary) {
}
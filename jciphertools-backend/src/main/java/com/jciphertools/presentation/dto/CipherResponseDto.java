package com.jciphertools.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Result of a cipher operation")
public record CipherResponseDto(
        @Schema(description = "Encrypted or decrypted output")
        String result
) {}

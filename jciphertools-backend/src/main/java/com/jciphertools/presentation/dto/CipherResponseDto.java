package com.jciphertools.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Result of a cipher operation")
public record CipherResponseDto(
        @Schema(description = "Output of the cipher operation. Encrypted values are Base64-encoded.")
        String result
) {}

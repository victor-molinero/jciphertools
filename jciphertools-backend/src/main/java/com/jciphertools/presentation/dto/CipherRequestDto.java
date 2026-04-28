package com.jciphertools.presentation.dto;

import com.jciphertools.domain.Algorithm;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request payload for cipher operations")
public record CipherRequestDto(
        @NotBlank
        @Schema(description = "Plain text to encrypt or Base64 ciphertext to decrypt", example = "Hello World")
        String input,

        @NotNull
        @Schema(description = "Cipher algorithm to use", example = "AES_CBC_256")
        Algorithm algorithm
) {}
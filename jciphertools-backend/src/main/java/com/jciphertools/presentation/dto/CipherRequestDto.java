package com.jciphertools.presentation.dto;

import com.jciphertools.domain.Algorithm;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request payload for cipher operations")
public record CipherRequestDto(
        @NotBlank
                @Schema(
                        description = "Input to process. Use plain text for /encrypt and Base64 ciphertext for /decrypt.",
                        example = "Hello World"
                )
        String input,

        @NotNull
                @Schema(
                        description = "Cipher algorithm to use. Allowed values: RSA_OAEP, AES_CBC_256, AES_GCM_256.",
                        example = "AES_CBC_256"
                )
        Algorithm algorithm
) {}
package com.jciphertools.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Supported cipher algorithms")
public enum Algorithm {
    @Schema(description = "RSA with OAEP padding")
    RSA_OAEP,
    @Schema(description = "AES-CBC with 256-bit key")
    AES_CBC_256,
    @Schema(description = "AES-GCM with 256-bit key")
    AES_GCM_256
}

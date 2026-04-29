package com.jciphertools.presentation.controllers;

import com.jciphertools.presentation.apidocs.ApiDocs;
import com.jciphertools.presentation.dto.CipherRequestDto;
import com.jciphertools.presentation.dto.CipherResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Cipher", description = "Encrypt and decrypt data")
public class CipherController {


    public CipherController() {

    }

    @PostMapping("/encrypt")
    @Operation(summary = "Encrypt input using the specified algorithm")
    @ApiResponse(responseCode = "200", description = "Encryption successful")
    @ApiDocs.CommonCipherErrorResponses
    public ResponseEntity<CipherResponseDto> encrypt(@Valid @RequestBody CipherRequestDto dto) {

        return ResponseEntity.ok(new CipherResponseDto("TODO: implement encryption logic"));
    }

    @PostMapping("/decrypt")
    @Operation(summary = "Decrypt input using the specified algorithm")
    @ApiResponse(responseCode = "200", description = "Decryption successful")
    @ApiDocs.CommonCipherErrorResponses
    public ResponseEntity<CipherResponseDto> decrypt(@Valid @RequestBody CipherRequestDto dto) {
        return ResponseEntity.ok(new CipherResponseDto("TODO: implement decryption logic"));
    }
}

package com.jciphertools.presentation.controllers;

import com.jciphertools.application.UseCases.DecryptUseCase;
import com.jciphertools.application.UseCases.EncryptUseCase;
import com.jciphertools.domain.CipherRequest;
import com.jciphertools.domain.CipherResponse;
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


    private final EncryptUseCase encryptUseCase;
    private final DecryptUseCase decryptUseCase;

    public CipherController(EncryptUseCase encryptUseCase, DecryptUseCase decryptUseCase) {
        this.encryptUseCase = encryptUseCase;
        this.decryptUseCase = decryptUseCase;
    }
    @PostMapping("/encrypt")
    @Operation(summary = "Encrypt input using the specified algorithm")
    @ApiResponse(responseCode = "200", description = "Encryption successful")
    @ApiDocs.CommonCipherErrorResponses
    public ResponseEntity<CipherResponseDto> encrypt(@Valid @RequestBody CipherRequestDto dto) {
        
        CipherRequest request = new CipherRequest(dto.input(), dto.algorithm());
        CipherResponse response = encryptUseCase.execute(request);
        return ResponseEntity.ok(new CipherResponseDto(response.result()));
    }

    @PostMapping("/decrypt")
    @Operation(summary = "Decrypt input using the specified algorithm")
    @ApiResponse(responseCode = "200", description = "Decryption successful")
    @ApiDocs.CommonCipherErrorResponses
    public ResponseEntity<CipherResponseDto> decrypt(@Valid @RequestBody CipherRequestDto dto) {
        CipherRequest request = new CipherRequest(dto.input(), dto.algorithm());
        CipherResponse response = decryptUseCase.execute(request);
        return ResponseEntity.ok(new CipherResponseDto(response.result()));
    }
}

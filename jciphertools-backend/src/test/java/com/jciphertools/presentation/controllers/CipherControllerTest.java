package com.jciphertools.presentation.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.jciphertools.application.UseCases.DecryptUseCase;
import com.jciphertools.application.UseCases.EncryptUseCase;
import com.jciphertools.domain.Algorithm;
import com.jciphertools.domain.CipherRequest;
import com.jciphertools.domain.CipherResponse;
import com.jciphertools.presentation.dto.CipherRequestDto;
import com.jciphertools.presentation.dto.CipherResponseDto;

@ExtendWith(MockitoExtension.class)
class CipherControllerTest {

    @Mock
    private EncryptUseCase encryptUseCase;

    @Mock
    private DecryptUseCase decryptUseCase;

    private CipherController controller;

    @BeforeEach
    void setUp() {
        controller = new CipherController(encryptUseCase, decryptUseCase);
    }

    @Test
    void shouldEncryptUsingUseCaseAndReturnOkResponse() {
        CipherRequestDto dto = new CipherRequestDto("hello", Algorithm.AES_CBC_256);
        when(encryptUseCase.execute(any(CipherRequest.class))).thenReturn(new CipherResponse("encrypted"));

        ResponseEntity<CipherResponseDto> response = controller.encrypt(dto);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("encrypted", response.getBody().result());

        ArgumentCaptor<CipherRequest> captor = ArgumentCaptor.forClass(CipherRequest.class);
        verify(encryptUseCase).execute(captor.capture());
        assertEquals("hello", captor.getValue().input());
        assertEquals(Algorithm.AES_CBC_256, captor.getValue().algorithm());
    }

    @Test
    void shouldDecryptUsingUseCaseAndReturnOkResponse() {
        CipherRequestDto dto = new CipherRequestDto("Y2lwaGVy", Algorithm.AES_GCM_256);
        when(decryptUseCase.execute(any(CipherRequest.class))).thenReturn(new CipherResponse("hello"));

        ResponseEntity<CipherResponseDto> response = controller.decrypt(dto);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("hello", response.getBody().result());

        ArgumentCaptor<CipherRequest> captor = ArgumentCaptor.forClass(CipherRequest.class);
        verify(decryptUseCase).execute(captor.capture());
        assertEquals("Y2lwaGVy", captor.getValue().input());
        assertEquals(Algorithm.AES_GCM_256, captor.getValue().algorithm());
    }
}
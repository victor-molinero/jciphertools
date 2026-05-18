package com.jciphertools.application.UseCases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jciphertools.application.Interfaces.CipherPort;
import com.jciphertools.domain.Algorithm;
import com.jciphertools.domain.CipherRequest;
import com.jciphertools.domain.CipherResponse;

@ExtendWith(MockitoExtension.class)
class DecryptUseCaseTest {

    @Mock
    private CipherPort cipherPort;

    private DecryptUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new DecryptUseCase(cipherPort);
    }

    @Test
    void shouldDelegateDecryptionToCipherPort() {
        CipherRequest request = new CipherRequest("ciphertext", Algorithm.AES_CBC_256);
        CipherResponse expected = new CipherResponse("hello");
        when(cipherPort.decrypt(request)).thenReturn(expected);

        CipherResponse actual = useCase.execute(request);

        assertSame(expected, actual);
        assertEquals("hello", actual.result());
        verify(cipherPort).decrypt(request);
    }

    @Test
    void shouldPropagateExceptionsFromCipherPort() {
        CipherRequest request = new CipherRequest("ciphertext", Algorithm.AES_CBC_256);
        IllegalStateException cause = new IllegalStateException("boom");
        when(cipherPort.decrypt(request)).thenThrow(cause);

        IllegalStateException actual = assertThrows(IllegalStateException.class, () -> useCase.execute(request));

        assertSame(cause, actual);
    }
}